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
import com.hazelcast.msfdemo.invsvc.events.InventoryGrpc;
import com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass;
import com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest;
import com.hazelcast.msfdemo.ordersvc.domain.Order;
import com.hazelcast.msfdemo.ordersvc.domain.WaitingOn;
import com.hazelcast.msfdemo.ordersvc.events.InventoryReserveEvent;
import com.hazelcast.msfdemo.ordersvc.events.OrderEvent;
import com.hazelcast.msfdemo.ordersvc.events.OrderEventTypes;
import com.hazelcast.msfdemo.ordersvc.events.PriceLookupEvent;
import com.hazelcast.msfdemo.ordersvc.eventstore.OrderEventStore;
import com.hazelcast.msfdemo.ordersvc.service.OrderService;
import io.grpc.ManagedChannelBuilder;

import java.io.File;
import java.util.AbstractMap;
import java.util.EnumSet;
import java.util.Map;

import static com.hazelcast.jet.datamodel.Tuple2.tuple2;
import static com.hazelcast.jet.grpc.GrpcServices.unaryService;

public class InventoryReservePipeline implements Runnable {

    private static OrderService service;

    private static String inventoryServiceHost;
    private static int inventoryServicePort;

    public InventoryReservePipeline(OrderService service) { InventoryReservePipeline.service = service; }

    @Override
    public void run() {
        try {
            MSFController controller = MSFController.getInstance();
            File f = new File("./order/target/OrderService-1.0-SNAPSHOT.jar");
            System.out.println("InventoryReservePipeline.run() invoked, submitting job");
            controller.startJob("OrderService", "OrderService.InventoryReserve", f, createPipeline());

            // Foreign service configuration
            ServiceConfig.ServiceProperties props = ServiceConfig.get("inventory-service");
            inventoryServiceHost = props.getHostname();
            inventoryServicePort = props.getPort();

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
        String eventStoreName = OrderEventStore.EVENT_STORE_NAME;
        IMap<Long,OrderEvent> eventStore = MSFController.getInstance().getMap(eventStoreName);
        // All jobs will share the same response map since all are part of the response
        // stream for the same request
        String responseMapName = OrderEventTypes.CREATE.getQualifiedName() + ".Results";
        IMap<Long, APIResponse<?>> responseMap = MSFController.getInstance().getMap(responseMapName);

        // Input from map journal has type Map.Entry<Long, OrderEvent>; once filtered
        // the items will be specifically MapEntry<Long,PriceLookupEvent>
        StreamStage<Map.Entry<Long, InventoryReserveEvent>> lookupStream = p.readFrom(Sources.mapJournal(eventStore,
                JournalInitialPosition.START_FROM_OLDEST))
                .withIngestionTimestamps()
                .setName("Read from " + eventStoreName)

                // We only want Price Lookup Events (our predecessor in the processing flow)
                .filter(entry -> entry.getValue().getEventName().equals(OrderEventTypes.PRICE_CALCULATED.getQualifiedName()))

                // Invoke the Inventory service's Reserve API  via gRPC
                .mapUsingServiceAsync(inventoryService, (service, eventEntry) -> {
                    System.out.println("Sending ReserveRequest to inventory service");
                    ReserveRequest request = ReserveRequest.newBuilder()
                                    .setItemNumber(eventEntry.getValue().getItemNumber())
                                    .setLocation(eventEntry.getValue().getLocation())
                                    .setQuantity(eventEntry.getValue().getQuantity())
                                    .build();

                    // Rather than restructure the pipeline to handle failure to
                    // reserve, we'll set a -1 quantity on failure and handle it
                    // accordingly downstream.
                    //InventoryReserveEvent reserve = new InventoryReserveEvent();
                    return service.call(request)
                            .thenApply(response -> {
                                // Get incoming event, will copy over fields that aren't changed.
                                // Might be better to get from DAO so we don't need to have
                                // full item content carried along with every event
                                //System.out.println("Received response from inventory reserve API: " + response.getSuccess());
                                // Looking for possibility of CCE here ..
                                PriceLookupEvent lookup = (PriceLookupEvent) eventEntry.getValue();
                                InventoryReserveEvent reserve = new InventoryReserveEvent();
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
                                    System.out.println("Service call to reserve inventory for " + lookup.getOrderNumber() + " failed becuase " + response.getReason());
                                }
                                // Non-request-specific fields carried over from last event
                                reserve.setOrderNumber(lookup.getOrderNumber());
                                reserve.setAccountNumber(lookup.getAccountNumber());
                                reserve.setExtendedPrice(lookup.getExtendedPrice());
                                return tuple2(eventEntry.getKey(), reserve);
                            });
                });

        // Persist to Event Store and Materialized View
        ServiceFactory<?, OrderEventStore> eventStoreServiceFactory =
                ServiceFactories.sharedService(
                        (ctx) -> OrderEventStore.getInstance()
                );

        ServiceFactory<?,IMap<String,Order>> materializedViewServiceFactory = ServiceFactories.iMapService(service.getView().getName());

        lookupStream.mapUsingService(eventStoreServiceFactory, (store, entry) -> {
            store.append(entry.getValue());
            return entry;
        }).setName("Persist InventoryReserveEvent to event store")

         // Create Materialized View object and publish it
        .mapUsingService(materializedViewServiceFactory, (viewMap, tuple)-> {
            InventoryReserveEvent invEvent = tuple.getValue();
            if (invEvent.getQuantity() != -1) {
                System.out.println("Updating Order (Materialized View) object with inv info");
                viewMap.executeOnKey(invEvent.getOrderNumber(), (EntryProcessor<String, Order, Order>) orderEntry -> {
                    Order orderView1 = orderEntry.getValue();
                    // Today this will always be a non-change, but possible we'd have an option
                    // to partially fill an order and inventory reserved might not match requested.
                    orderView1.setQuantity(invEvent.getQuantity());
                    EnumSet<WaitingOn> waits = orderView1.getWaitingOn();
                    waits.remove(WaitingOn.RESERVE_INVENTORY);
                    if (waits.isEmpty()) {
                        waits.add(WaitingOn.CHARGE_ACCOUNT);
                        waits.add(WaitingOn.PULL_INVENTORY);
                    }
                    return orderView1;
                });
            }
            //System.out.println("View object updated with reserved inventory");
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
