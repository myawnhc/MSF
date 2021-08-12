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
import com.hazelcast.msf.messaging.StreamObserverToIMapAdapter;
import com.hazelcast.msfdemo.invsvc.events.InventoryGrpc;
import com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass;
import com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest;
import com.hazelcast.msfdemo.ordersvc.domain.Order;
import com.hazelcast.msfdemo.ordersvc.domain.WaitingOn;
import com.hazelcast.msfdemo.ordersvc.events.AccountInventoryCombo;
import com.hazelcast.msfdemo.ordersvc.events.InventoryReserveEvent;
import com.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderPriced;
import com.hazelcast.msfdemo.ordersvc.events.PriceLookupEvent;
import com.hazelcast.msfdemo.ordersvc.eventstore.OrderEventStore;
import com.hazelcast.msfdemo.ordersvc.service.OrderService;
import com.hazelcast.msfdemo.ordersvc.views.OrderDAO;
import io.grpc.ManagedChannelBuilder;

import java.io.File;
import java.util.EnumSet;

import static com.hazelcast.jet.grpc.GrpcServices.unaryService;

public class InventoryReservePipeline implements Runnable {

    private static OrderService orderService;
    private static String inventoryServiceHost;
    private static int inventoryServicePort;
    private static IMap<Long, OrderPriced> orderPricedEvents;

    private static final String PENDING_MAP_NAME = "pendingValidation";
    private static final String COMPLETED_MAP_NAME = "JRN.completedValidation";

    public InventoryReservePipeline(OrderService service) {
        InventoryReservePipeline.orderService = service;
    }

    @Override
    public void run() {
        try {
            MSFController controller = MSFController.getInstance();

            // Foreign service configuration
            ServiceConfig.ServiceProperties props = ServiceConfig.get("inventory-service");
            inventoryServiceHost = props.getGrpcHostname();
            inventoryServicePort = props.getGrpcPort();

            // Reserve inventory is triggered by order pricing
            String mapName = "JRN.IRP." + OrderPriced.getDescriptor().getFullName();
            orderPricedEvents = controller.getMap(mapName);
            IAtomicLong sequence = controller.getSequenceGenerator(mapName);
            PriceLookupEvent.subscribe(new StreamObserverToIMapAdapter<>(orderPricedEvents, sequence));

            // Build pipeline and submit job
            File f = new File("./order/target/OrderService-1.0-SNAPSHOT.jar");
            System.out.println("InventoryReservePipeline.run() invoked, submitting job");
            controller.startJob("OrderService", "OrderService.InventoryReserve", f, createPipeline());
        } catch (Exception e) { // Happens if our pipeline is not valid
            e.printStackTrace();
        }
    }

    private static Pipeline createPipeline() {

        ServiceFactory<?, ? extends GrpcService<ReserveRequest, InventoryOuterClass.ReserveResponse>>
               inventoryService = unaryService(
                () -> ManagedChannelBuilder.forAddress(inventoryServiceHost, inventoryServicePort) .usePlaintext(),
                channel -> InventoryGrpc.newStub(channel)::reserve);

        Pipeline p = Pipeline.create();

        StreamStage<InventoryReserveEvent> reserveEvents = p.readFrom(Sources.mapJournal(orderPricedEvents,
                JournalInitialPosition.START_FROM_OLDEST))
                .withIngestionTimestamps()
                .setName("Read from " + orderPricedEvents.getName())

                // Invoke the Inventory service's Reserve API  via gRPC
                .mapUsingServiceAsync(inventoryService, (service, eventEntry) -> {
                    System.out.println("Sending ReserveRequest to inventory service");
                    OrderPriced ple = eventEntry.getValue();
                    OrderDAO dao = orderService.getDAO();
                    Order order = dao.findByKey(ple.getOrderNumber());
                    ReserveRequest request = ReserveRequest.newBuilder()
                                    .setItemNumber(order.getItemNumber())
                                    .setLocation(order.getLocation())
                                    .setQuantity(order.getQuantity())
                                    .build();

                    // Rather than restructure the pipeline to handle failure to
                    // reserve, we'll set a -1 quantity on failure and handle it
                    // accordingly downstream.
                    return service.call(request)
                            .thenApply(response -> {
                                OrderPriced lookup = eventEntry.getValue();
                                InventoryReserveEvent reserve = new InventoryReserveEvent(lookup.getOrderNumber());
                                reserve.setItemNumber(request.getItemNumber());
                                reserve.setLocation(request.getLocation());
                                // TODO: what is the desired behavior on failure?  Log event with zero
                                //  quantity, log nothing (which short-circuits remaining stages?)
                                //  Will revisit, but leaning toward log nothing, skip down to sending
                                //  API response to client who can decide to retry, etc.
                                if (response.getSuccess()) {
                                    reserve.setQuantity(request.getQuantity());
                                    //System.out.println("Service call to reserve inventory for " + lookup.getOrderNumber() + " success");
                                } else {
                                    reserve.setQuantity(-1);
                                    reserve.setFailureReason(response.getReason());
                                    System.out.println("Service call to reserve inventory for " + lookup.getOrderNumber() + " failed because " + response.getReason());
                                }
                                // Non-request-specific fields carried over from last event
                                reserve.setOrderNumber(lookup.getOrderNumber());
                                reserve.setAccountNumber(order.getAcctNumber());
                                //return tuple2(eventEntry.getKey(), reserve);
                                return reserve;
                            });
                });

        // Persist to Event Store and Materialized View
        ServiceFactory<?, OrderEventStore> eventStoreServiceFactory =
                ServiceFactories.sharedService(
                        (ctx) -> OrderEventStore.getInstance()
                );

        ServiceFactory<?,IMap<String,Order>> materializedViewServiceFactory =
                ServiceFactories.iMapService(orderService.getView().getName());

        ServiceFactory<?, IMap<String, AccountInventoryCombo>> pendingMapService =
                ServiceFactories.iMapService(PENDING_MAP_NAME);

//        ServiceFactory<?, IMap<String, AccountInventoryCombo>> completedMapService =
//                ServiceFactories.iMapService(COMPLETED_MAP_NAME);

        // Append to Event Store
        StreamStage<InventoryReserveEvent> persistedEvents = reserveEvents.mapUsingService(eventStoreServiceFactory, (store, event) -> {
            store.append(event);
            return event;
        }).setName("Persist InventoryReserveEvent to event store");

        // Update Materialized View
        persistedEvents.mapUsingService(materializedViewServiceFactory, (viewMap, invEvent) -> {
            // Types for viewMap, invEvent are unknown here but resolve fine in nearly-identical CreditCheckPipeline
            Order order = viewMap.executeOnKey(invEvent.getOrderNumber(), (EntryProcessor<String, Order, Order>) orderEntry -> {
                Order orderView1 = orderEntry.getValue();
                // Today this will always be a non-change, but possible we'd have an option
                // to partially fill an order and inventory reserved might not match requested.
                orderView1.setQuantity(invEvent.getQuantity());
                EnumSet<WaitingOn> waits = orderView1.getWaitingOn();
                waits.remove(WaitingOn.RESERVE_INVENTORY);
                System.out.println("After removing ReserveInventory, waiting on: " + waits.toString());
                if (waits.isEmpty()) {
                    waits.add(WaitingOn.CHARGE_ACCOUNT);
                    waits.add(WaitingOn.PULL_INVENTORY);
                }
                orderEntry.setValue(orderView1);
                return orderView1;
            });
            return invEvent;
        })
                //System.out.println("View object updated with reserved inventory");
                //return tuple;
                .setName("Update Order Materialized View")
                .mapUsingService(pendingMapService, (map, irevent) -> {
                    String orderNumber = irevent.getOrderNumber();
                    AccountInventoryCombo combo = map.get(orderNumber);
                    if (combo != null) {
                        // validate
                        if (!combo.hasAccountFields()) {
                            System.out.println("WARNING: pending entry has no account data");
                        }
                        combo.setInventoryFields(irevent);
                        map.remove(irevent);
                        System.out.println("Combo completed with inventory fields " + combo);
                        return combo;
                    } else {
                        combo = new AccountInventoryCombo();
                        combo.setInventoryFields(irevent);
                        map.set(combo.getOrderNumber(), combo);
                        System.out.println("Combo created with inventory fields");
                        return null;
                    }

                })
                .setName("Merge inventory and account results into combo item")
                .writeTo(Sinks.map(COMPLETED_MAP_NAME,
                                /* toKeyFn*/ combo -> combo.getOrderNumber(),
                        /* toValueFn */ combo -> combo))
                .setName("Sink inv-acct combo item into map");
        return p;
    }
}
