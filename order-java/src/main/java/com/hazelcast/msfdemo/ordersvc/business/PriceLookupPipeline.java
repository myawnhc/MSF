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
import com.hazelcast.msfdemo.ordersvc.domain.Order;
import com.hazelcast.msfdemo.ordersvc.domain.WaitingOn;
import com.hazelcast.msfdemo.ordersvc.events.CreateOrderEvent;
import com.hazelcast.msfdemo.ordersvc.events.OrderEvent;
import com.hazelcast.msfdemo.ordersvc.events.OrderEventTypes;
import com.hazelcast.msfdemo.ordersvc.events.PriceLookupEvent;
import com.hazelcast.msfdemo.ordersvc.eventstore.OrderEventStore;
import com.hazelcast.msfdemo.ordersvc.service.OrderService;
import com.hazelcast.msfdemo.protosvc.events.CatalogGrpc;
import com.hazelcast.msfdemo.protosvc.events.CatalogOuterClass.PriceLookupRequest;
import io.grpc.ManagedChannelBuilder;

import java.io.File;
import java.util.AbstractMap;
import java.util.EnumSet;
import java.util.Map;

import static com.hazelcast.jet.datamodel.Tuple2.tuple2;
import static com.hazelcast.jet.grpc.GrpcServices.unaryService;
import static com.hazelcast.msfdemo.protosvc.events.CatalogOuterClass.PriceLookupResponse;

public class PriceLookupPipeline implements Runnable {

    private static OrderService service;

    private static String priceLookupServiceHost;
    private static int priceLookupServicePort;

    public PriceLookupPipeline(OrderService service) { PriceLookupPipeline.service = service; }

    @Override
    public void run() {
        try {
            MSFController controller = MSFController.getInstance();
            File f = new File("./order/target/OrderService-1.0-SNAPSHOT.jar");
            System.out.println("PriceLookupPipeline.run() invoked, submitting job");
            controller.startJob("OrderService", "OrderService.PriceLookup", f, createPipeline());

            // Foreign service configuration
            ServiceConfig.ServiceProperties props = ServiceConfig.get("catalog-service");
            priceLookupServiceHost = props.getHostname();
            priceLookupServicePort = props.getPort();

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
        String eventStoreName = OrderEventStore.EVENT_STORE_NAME;
        IMap<Long,OrderEvent> eventStore = MSFController.getInstance().getMap(eventStoreName);
        // All jobs will share the same response map since all are part of the response
        // stream for the same request
        String responseMapName = OrderEventTypes.CREATE.getQualifiedName() + ".Results";
        IMap<Long, APIResponse<?>> responseMap = MSFController.getInstance().getMap(responseMapName);

        // Input from map journal has type Map.Entry<Long, OrderEvent>; once filtered
        // the items will be specifically MapEntry<Long,CreateOrderEvent>
        StreamStage<Map.Entry<Long,PriceLookupEvent>> lookupStream = p.readFrom(Sources.mapJournal(eventStore,
                JournalInitialPosition.START_FROM_OLDEST))
                .withIngestionTimestamps()
                .setName("Read from " + eventStoreName)

                // We only want Create Order Events
                .filter(entry -> entry.getValue().getEventName().equals(OrderEventTypes.CREATE.getQualifiedName()))

                // Invoke the Catalog price lookup service via gRPC
                .mapUsingServiceAsync(priceLookupService, (service, eventEntry) -> {
                            PriceLookupRequest request = PriceLookupRequest.newBuilder()
                                    .setItemNumber(eventEntry.getValue().getItemNumber())
                                    .build();

                            return service.call(request)
                                    .thenApply(response -> {
                                        CreateOrderEvent create = (CreateOrderEvent) eventEntry.getValue();
                                        PriceLookupEvent lookup = new PriceLookupEvent(create.getOrderNumber(),
                                                create.getAccountNumber(), create.getItemNumber(), create.getLocation(),
                                                create.getQuantity(), response.getPrice());
                                        //System.out.println("Lookup " + lookup.getOrderNumber() + " now priced " + lookup.getExtendedPrice());
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
                return orderView1;
            });
            //System.out.println("View object updated with extended price");
            return tuple;
        }).setName("Update Order Materialized View")

        // Write APIResponse to result map, triggering gRPC response to client
        .map( tuple -> {
            Long uniqueID = tuple.getKey();
            OrderEvent event = tuple.getValue();
            APIResponse<OrderEvent> response = new APIResponse<>(uniqueID, event);
            //System.out.println("Building and returning API response");
            return new AbstractMap.SimpleEntry<>(uniqueID, response);
        }).setName("Build client APIResponse")
                .writeTo(Sinks.map(responseMap))
                .setName("Write result to response map (triggers response to client)");

        return p;
    }
}
