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
import com.hazelcast.msfdemo.invsvc.events.InventoryGrpc;
import com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass;
import com.hazelcast.msfdemo.ordersvc.domain.Order;
import com.hazelcast.msfdemo.ordersvc.domain.WaitingOn;
import com.hazelcast.msfdemo.ordersvc.events.AccountInventoryCombo;
import com.hazelcast.msfdemo.ordersvc.events.PullInventoryEvent;
import com.hazelcast.msfdemo.ordersvc.eventstore.OrderEventStore;
import com.hazelcast.msfdemo.ordersvc.service.OrderService;
import io.grpc.ManagedChannelBuilder;

import java.io.File;
import java.util.EnumSet;

import static com.hazelcast.jet.grpc.GrpcServices.unaryService;

public class PullInventoryPipeline implements Runnable {

    private static OrderService orderService;
    private static String inventoryServiceHost;
    private static int inventoryServicePort;
    private static IMap<String, AccountInventoryCombo> acctInvCombos;

    public PullInventoryPipeline(OrderService service) {
        PullInventoryPipeline.orderService = service;
    }

    @Override
    public void run() {
        try {
            MSFController controller = MSFController.getInstance();

            // Foreign service configuration
            ServiceConfig.ServiceProperties props = ServiceConfig.get("inventory-service");
            inventoryServiceHost = props.getGrpcHostname();
            inventoryServicePort = props.getGrpcPort();

            // We pull from map that has merged events
            String comboMap = "JRN.completedValidation";
            acctInvCombos = controller.getMap(comboMap);

            // Build pipeline and submit job
            File f = new File("./order/target/OrderService-1.0-SNAPSHOT.jar");
            System.out.println("PullInventoryPipeline.run() invoked, submitting job");
            controller.startJob("OrderService", "OrderService.PullInventory", f, createPipeline());

        } catch (Exception e) { // Happens if pipeline is not valid
            e.printStackTrace();
        }
    }

    private static Pipeline createPipeline() {

        // Remote gRPC service (InventoryService PullInventory )
        ServiceFactory<?, ? extends GrpcService<InventoryOuterClass.PullRequest, InventoryOuterClass.PullResponse>>
                inventoryService = unaryService(
                () -> ManagedChannelBuilder.forAddress(inventoryServiceHost, inventoryServicePort) .usePlaintext(),
                channel -> InventoryGrpc.newStub(channel)::pull);


        // EventStore as a service
        ServiceFactory<?, OrderEventStore> eventStoreServiceFactory =
                ServiceFactories.sharedService((ctx) -> OrderEventStore.getInstance());

        // IMap/Materialized View as a service
        ServiceFactory<?, IMap<String,Order>> materializedViewServiceFactory =
                ServiceFactories.iMapService(orderService.getView().getName());

        Pipeline p = Pipeline.create();

        StreamStage<AccountInventoryCombo> combos = p.readFrom(Sources.mapJournal(
                acctInvCombos,
                JournalInitialPosition.START_FROM_OLDEST))
                .withIngestionTimestamps()
                .setName("Read from " + acctInvCombos.getName())
                .map( entry -> entry.getValue());

        // Enrichment stage not needed, combo object
        StreamStage<PullInventoryEvent> pullevents = combos.mapUsingServiceAsync(inventoryService, (service, combo) -> {
            String item = combo.getItemNumber();
            String location = combo.getLocation();
            int quantity = combo.getQuantity();
            System.out.println("PullInventoryPipeline - Sending Pull request to account service");
            InventoryOuterClass.PullRequest request = InventoryOuterClass.PullRequest.newBuilder()
                    .setItemNumber(item)
                    .setLocation(location)
                    .setQuantity(quantity)
                    .build();
            return service.call(request)
                    .thenApply(response -> {
                        PullInventoryEvent pullEvent = new PullInventoryEvent(combo.getOrderNumber());
                        pullEvent.setItemNumber(item);
                        pullEvent.setLocation(location);
                        pullEvent.setQuantityPulled(quantity);
                        return pullEvent;
                    });
        });

        // Write ChargeAccountEvent to Event Store
        pullevents.mapUsingService(eventStoreServiceFactory, (store, payment) -> {
                    store.append(payment);
                    return payment;
                }).setName("Persist PullInventoryEvent to event store")

        // Update Materialized View including wait flags
        .mapUsingService(materializedViewServiceFactory, (viewMap, pullEvent) -> {
            viewMap.executeOnKey(pullEvent.getOrderNumber(), (EntryProcessor<String, Order, Order>) orderEntry -> {
                Order orderView = orderEntry.getValue();
                EnumSet<WaitingOn> waits = orderView.getWaitingOn();
                waits.remove(WaitingOn.PULL_INVENTORY);
                if (waits.isEmpty()) {
                    waits.add(WaitingOn.SHIP);
                }
                System.out.println("After removing PULL_INVENTORY, waiting on: " + waits.toString());
                orderEntry.setValue(orderView);
                return orderView;
            });
            return pullEvent;
        }).setName("Update order Materialized View")
                .writeTo(Sinks.noop());
        return p;
    }
}
