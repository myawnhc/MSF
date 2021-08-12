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

import com.hazelcast.jet.pipeline.JournalInitialPosition;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.ServiceFactories;
import com.hazelcast.jet.pipeline.ServiceFactory;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.Sources;
import com.hazelcast.jet.pipeline.StreamStage;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.IMap;
import com.hazelcast.msf.controller.MSFController;
import com.hazelcast.msfdemo.ordersvc.domain.Order;
import com.hazelcast.msfdemo.ordersvc.domain.WaitingOn;
import com.hazelcast.msfdemo.ordersvc.events.AccountInventoryCombo;
import com.hazelcast.msfdemo.ordersvc.events.ShipEvent;
import com.hazelcast.msfdemo.ordersvc.eventstore.OrderEventStore;
import com.hazelcast.msfdemo.ordersvc.service.OrderService;

import java.io.File;
import java.util.EnumSet;

public class ShipPipeline implements Runnable {

    private static OrderService orderService;
    private static IMap<String, AccountInventoryCombo> acctInvCombos;

    public ShipPipeline(OrderService service) {
        ShipPipeline.orderService = service;
    }

    @Override
    public void run() {
        try {
            MSFController controller = MSFController.getInstance();

            // We pull from map that has merged events
            String comboMap = "JRN.completedTransactions";
            acctInvCombos = controller.getMap(comboMap);

            // Build pipeline and submit job
            File f = new File("./order/target/OrderService-1.0-SNAPSHOT.jar");
            System.out.println("ShipPipeline.run() invoked, submitting job");
            controller.startJob("OrderService", "OrderService.Ship", f, createPipeline());

        } catch (Exception e) { // Happens if pipeline is not valid
            e.printStackTrace();
        }
    }

    private static Pipeline createPipeline() {

        // Remote gRPC service (InventoryService PullInventory )
//        ServiceFactory<?, ? extends GrpcService<InventoryOuterClass.PullRequest, InventoryOuterClass.PullResponse>>
//                inventoryService = unaryService(
//                () -> ManagedChannelBuilder.forAddress(inventoryServiceHost, inventoryServicePort) .usePlaintext(),
//                channel -> InventoryGrpc.newStub(channel)::pull);


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

        // Create ShipEvent
        StreamStage<ShipEvent> shipEvents = combos.map(combo -> {
            ShipEvent shipment = new ShipEvent(combo.getOrderNumber());
            shipment.setItemNumber(combo.getItemNumber());
            shipment.setQuantity(combo.getQuantity());
            return shipment;
        });

        // Write ShipEvent to Event Store
        shipEvents.mapUsingService(eventStoreServiceFactory, (store, shipment) -> {
                    store.append(shipment);
                    return shipment;
                }).setName("Persist ShipEvent to event store")

        // Update Materialized View including wait flags
        .mapUsingService(materializedViewServiceFactory, (viewMap, shipEvent) -> {
            viewMap.executeOnKey(shipEvent.getOrderNumber(), (EntryProcessor<String, Order, Order>) orderEntry -> {
                Order orderView = orderEntry.getValue();
                EnumSet<WaitingOn> waits = orderView.getWaitingOn();
                waits.remove(WaitingOn.SHIP);
                if (waits.isEmpty()) {
                    waits.add(WaitingOn.NOTHING);
                }
                System.out.println("After removing SHIP, waiting on: " + waits.toString());
                orderEntry.setValue(orderView);
                return orderView;
            });
            return shipEvent;
        }).setName("Update Order Materialized View")
                .writeTo(Sinks.noop());
        return p;
    }
}
