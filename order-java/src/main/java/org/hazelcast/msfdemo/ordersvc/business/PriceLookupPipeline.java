/*
 * Copyright 2018-2022 Hazelcast, Inc
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
 *  limitations under the License.
 */

package org.hazelcast.msfdemo.ordersvc.business;

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
import io.grpc.ManagedChannelBuilder;
import org.hazelcast.msf.configuration.ServiceConfig;
import org.hazelcast.msf.controller.MSFController;
import org.hazelcast.msf.messaging.StreamObserverToIMapAdapter;
import org.hazelcast.msfdemo.ordersvc.domain.Order;
import org.hazelcast.msfdemo.ordersvc.domain.WaitingOn;
import org.hazelcast.msfdemo.ordersvc.events.CreateOrderEvent;
import org.hazelcast.msfdemo.ordersvc.events.PriceLookupEvent;
import org.hazelcast.msfdemo.ordersvc.eventstore.OrderEventStore;
import org.hazelcast.msfdemo.ordersvc.service.OrderService;
import org.hazelcast.msfdemo.protosvc.events.CatalogGrpc;
import org.hazelcast.msfdemo.protosvc.events.CatalogOuterClass;

import java.util.EnumSet;
import java.util.Map;

import static com.hazelcast.jet.datamodel.Tuple2.tuple2;
import static com.hazelcast.jet.grpc.GrpcServices.unaryService;
import static org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderCreated;

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
            System.out.println("OrderService.PriceLookup will connect to catalog-service @ " + priceLookupServiceHost + ":" + priceLookupServicePort);

            // Price Lookup is invoked as soon as an order is created.  So we subscribe to
            // OrderCreated notifications to initiate a pipeline entry
            String mapName = "JRN."+ "PLP." + OrderCreated.getDescriptor().getFullName();
            orderCreatedEvents = controller.getMap(mapName);
            IAtomicLong sequence = controller.getSequenceGenerator(mapName);
            // If we subscribe before the Event class is initialized it NPEs so we do our own init.
            // This really breaks encapsulation, should probably pass instance to subscribe instead.
            CreateOrderEvent.setHazelcastInstance(controller.getHazelcastInstance());
            CreateOrderEvent.subscribe(new StreamObserverToIMapAdapter<>(orderCreatedEvents, sequence));

            // Build pipeline and submit job
            //File f = new File("./order/target/OrderService-1.0-SNAPSHOT.jar");
            System.out.println("PriceLookupPipeline.run() invoked, submitting job");
            controller.startJob("OrderService", "OrderService.PriceLookup", createPipeline());
        } catch (Exception e) { // Happens if our pipeline is not valid
            e.printStackTrace();
        }
    }

    private static Pipeline createPipeline() {
        try {
            ServiceFactory<?, ? extends GrpcService<CatalogOuterClass.PriceLookupRequest, CatalogOuterClass.PriceLookupResponse>>
                    s1 = unaryService(() -> ManagedChannelBuilder.forAddress("localhost", priceLookupServicePort).usePlaintext(),
                    channel -> CatalogGrpc.newStub(channel)::priceLookup
            );
            System.out.println("localhost OK");
        } catch (Exception e) {
            System.out.println("localhost FAILED");
        }
        try {
            ServiceFactory<?, ? extends GrpcService<CatalogOuterClass.PriceLookupRequest, CatalogOuterClass.PriceLookupResponse>>
                    s2 = unaryService(() -> ManagedChannelBuilder.forAddress("172.19.0.5", priceLookupServicePort).usePlaintext(),
                channel -> CatalogGrpc.newStub(channel)::priceLookup
            );
            System.out.println("docker IP OK");
        } catch (Exception e) {
            System.out.println("docker IP FAILED");
        }
        try {
            ServiceFactory<?, ? extends GrpcService<CatalogOuterClass.PriceLookupRequest, CatalogOuterClass.PriceLookupResponse>>
                    s3 = unaryService(() -> ManagedChannelBuilder.forAddress("catalogsvc", priceLookupServicePort).usePlaintext(),
                    channel -> CatalogGrpc.newStub(channel)::priceLookup
            );
            System.out.println("service name OK");
        } catch (Exception e) {
            System.out.println("service name FAILED");
        }
        try {
            ServiceFactory<?, ? extends GrpcService<CatalogOuterClass.PriceLookupRequest, CatalogOuterClass.PriceLookupResponse>>
                    s4 = unaryService(() -> ManagedChannelBuilder.forAddress("192.168.86.25", priceLookupServicePort).usePlaintext(),
                    channel -> CatalogGrpc.newStub(channel)::priceLookup
            );
            System.out.println("actual local IP OK");
        } catch (Exception e) {
            System.out.println("actual local IP FAILED");
        }
        try {
            ServiceFactory<?, ? extends GrpcService<CatalogOuterClass.PriceLookupRequest, CatalogOuterClass.PriceLookupResponse>>
                    s5 = unaryService(() -> ManagedChannelBuilder.forAddress("127.0.0.1", priceLookupServicePort).usePlaintext(),
                    channel -> CatalogGrpc.newStub(channel)::priceLookup
            );
            System.out.println("loopback OK");
        } catch (Exception e) {
            System.out.println("lookback FAILED");
        }

        ServiceFactory<?, ? extends GrpcService<CatalogOuterClass.PriceLookupRequest, CatalogOuterClass.PriceLookupResponse>>
                priceLookupService = null;
        try {
            priceLookupService = unaryService(
                    () -> ManagedChannelBuilder.forAddress(priceLookupServiceHost, priceLookupServicePort).usePlaintext(),
                    channel -> CatalogGrpc.newStub(channel)::priceLookup
            );
            System.out.println("Managed Channel for PriceLookupService @ "+ priceLookupServiceHost + ":" + priceLookupServicePort + " OK");

        } catch (Exception e) {
            System.out.println("Managed Channel for PriceLookupService @ "+ priceLookupServiceHost + ":" + priceLookupServicePort + "failed");
            e.printStackTrace();
        }

        Pipeline p = Pipeline.create();

        StreamStage<Map.Entry<Long,PriceLookupEvent>> lookupStream = p.readFrom(Sources.mapJournal(orderCreatedEvents,
                JournalInitialPosition.START_FROM_OLDEST))
                .withIngestionTimestamps()
                .setName("Read from " + orderCreatedEvents.getName())

                // Invoke the Catalog price lookup service via gRPC
                .mapUsingServiceAsync(priceLookupService, (service, eventEntry) -> {
                            OrderCreated orderCreated = eventEntry.getValue();
                            CatalogOuterClass.PriceLookupRequest request = CatalogOuterClass.PriceLookupRequest.newBuilder()
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
                        (ctx) -> new OrderEventStore(ctx.hazelcastInstance())
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
