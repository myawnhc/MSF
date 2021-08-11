/*
 * Copyright 2018-2021 Hazelcast, Inc
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.package com.theyawns.controller.launcher;
 *
 */

package com.hazelcast.msfdemo.ordersvc.business;

import com.hazelcast.cp.IAtomicLong;
import com.hazelcast.jet.grpc.GrpcService;
import com.hazelcast.jet.pipeline.JournalInitialPosition;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.ServiceFactories;
import com.hazelcast.jet.pipeline.ServiceFactory;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.Sources;
import com.hazelcast.jet.pipeline.StreamStage;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.IMap;
import com.hazelcast.msf.configuration.ServiceConfig;
import com.hazelcast.msf.controller.MSFController;
import com.hazelcast.msf.messaging.APIResponse;
import com.hazelcast.msf.messaging.StreamObserverToIMapAdapter;
import com.hazelcast.msfdemo.ordersvc.domain.Order;
import com.hazelcast.msfdemo.ordersvc.domain.WaitingOn;
import com.hazelcast.msfdemo.ordersvc.events.CreateOrderEvent;
import com.hazelcast.msfdemo.ordersvc.events.OrderEventTypes;
import com.hazelcast.msfdemo.ordersvc.events.PriceLookupEvent;
import com.hazelcast.msfdemo.ordersvc.eventstore.OrderEventStore;
import com.hazelcast.msfdemo.ordersvc.service.OrderService;
import com.hazelcast.msfdemo.protosvc.events.CatalogGrpc;
import com.hazelcast.msfdemo.protosvc.events.CatalogOuterClass.PriceLookupRequest;
import io.grpc.ManagedChannelBuilder;

import java.io.File;
import java.util.EnumSet;
import java.util.Map;

import static com.hazelcast.jet.datamodel.Tuple2.tuple2;
import static com.hazelcast.jet.grpc.GrpcServices.unaryService;
import static com.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderCreated;
import static com.hazelcast.msfdemo.protosvc.events.CatalogOuterClass.PriceLookupResponse;

public class PriceLookupPipeline implements Runnable {

    private static OrderService service;
    private static String priceLookupServiceHost;
    private static int priceLookupServicePort;
    private static IMap<Long, OrderCreated> orderCreatedEvents;

    public PriceLookupPipeline(OrderService service) { PriceLookupPipeline.service = service; }

    @Override
    public void run() {
        try {
            MSFController controller = MSFController.getInstance();

            // Foreign service configuration
            ServiceConfig.ServiceProperties props = ServiceConfig.get("catalog-service");
            priceLookupServiceHost = props.getGrpcHostname();
            priceLookupServicePort = props.getGrpcPort();

            // Price Lookup is invoked as soon as an order is created.  So we subscribe to
            // OrderCreated notifications to initiate a pipeline entry
            String mapName = "JRN."+ "PLP." + OrderCreated.getDescriptor().getFullName();
            orderCreatedEvents = controller.getMap(mapName);
            IAtomicLong sequence = controller.getSequenceGenerator(mapName);
            CreateOrderEvent.subscribe(new StreamObserverToIMapAdapter<>(orderCreatedEvents, sequence));

            // Build pipeline and submit job
            File f = new File("./order/target/OrderService-1.0-SNAPSHOT.jar");
            System.out.println("PriceLookupPipeline.run() invoked, submitting job");
            controller.startJob("OrderService", "OrderService.PriceLookup", f, createPipeline());
        } catch (Exception e) { // Happens if our pipeline is not valid
            e.printStackTrace();
        }
    }

    private static Pipeline createPipeline() {

        ServiceFactory<?, ? extends GrpcService<PriceLookupRequest, PriceLookupResponse>>
                priceLookupService = unaryService(
                () -> ManagedChannelBuilder.forAddress(priceLookupServiceHost, priceLookupServicePort) .usePlaintext(),
                channel -> CatalogGrpc.newStub(channel)::priceLookup
        );

        Pipeline p = Pipeline.create();
        String responseMapName = OrderEventTypes.CREATE.getQualifiedName() + ".Results";
        IMap<Long, APIResponse<?>> responseMap = MSFController.getInstance().getMap(responseMapName);

        StreamStage<Map.Entry<Long,PriceLookupEvent>> lookupStream = p.readFrom(Sources.mapJournal(orderCreatedEvents,
                JournalInitialPosition.START_FROM_OLDEST))
                .withIngestionTimestamps()
                .setName("Read from " + orderCreatedEvents.getName())

                // Invoke the Catalog price lookup service via gRPC
                .mapUsingServiceAsync(priceLookupService, (service, eventEntry) -> {
                            OrderCreated orderCreated = eventEntry.getValue();
                            PriceLookupRequest request = PriceLookupRequest.newBuilder()
                                    .setItemNumber(orderCreated.getItemNumber())
                                    .build();

                            return service.call(request)
                                    .thenApply(response -> {
                                        PriceLookupEvent lookup = new PriceLookupEvent(orderCreated.getOrderNumber(),
                                                orderCreated.getItemNumber(), orderCreated.getLocation(),
                                                orderCreated.getQuantity(), response.getPrice());
                                        return tuple2(eventEntry.getKey(), lookup);
                                    });
                        });

        // Persist to Event Store and Materialized View
        ServiceFactory<?, OrderEventStore> eventStoreServiceFactory =
                ServiceFactories.sharedService(
                        (ctx) -> OrderEventStore.getInstance()
                );

        ServiceFactory<?,IMap<String,Order>> materializedViewServiceFactory = ServiceFactories.iMapService(service.getView().getName());

        lookupStream.mapUsingService(eventStoreServiceFactory, (store, lookupEntry) -> {
            store.append(lookupEntry.getValue());
            return lookupEntry;
        }).setName("Persist PriceCalculatedEvent to event store")

         // Create Materialized View object and publish it
        .mapUsingService(materializedViewServiceFactory, (viewMap, tuple)-> {
            PriceLookupEvent orderEvent = tuple.getValue();
            viewMap.executeOnKey(orderEvent.getOrderNumber(), (EntryProcessor<String, Order, Order>) orderEntry -> {
                Order orderView1 = orderEntry.getValue();
                orderView1.setExtendedPrice(orderEvent.getExtendedPrice());
                orderView1.setWaitingOn(EnumSet.of(WaitingOn.CREDIT_CHECK, WaitingOn.RESERVE_INVENTORY));
                System.out.println("PLP updates wait flags to " + orderView1.getWaitingOn().toString());
                orderEntry.setValue(orderView1);
                return orderView1;
            });
            return tuple;
        }).setName("Update Order Materialized View")

        // Write APIResponse to result map, triggering gRPC response to client
//        .map( tuple -> {
//            Long uniqueID = tuple.getKey();
//            OrderEvent event = tuple.getValue();
//            APIResponse<OrderEvent> response = new APIResponse<>(uniqueID, event);
//            //System.out.println("Building and returning API response");
//            return new AbstractMap.SimpleEntry<>(uniqueID, response);
//        }).setName("Build client APIResponse")
                .writeTo(Sinks.noop())
                .setName("Nothing to sink");

        return p;
    }
}
