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

import com.hazelcast.jet.pipeline.JournalInitialPosition;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.ServiceFactories;
import com.hazelcast.jet.pipeline.ServiceFactory;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.Sources;
import com.hazelcast.jet.pipeline.StreamStage;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.IMap;
import org.hazelcast.msf.controller.MSFController;
import org.hazelcast.msfdemo.ordersvc.domain.Order;
import org.hazelcast.msfdemo.ordersvc.domain.WaitingOn;
import org.hazelcast.msfdemo.ordersvc.events.AccountInventoryCombo;
import org.hazelcast.msfdemo.ordersvc.events.OrderShippedEvent;
import org.hazelcast.msfdemo.ordersvc.eventstore.OrderEventStore;
import org.hazelcast.msfdemo.ordersvc.service.OrderService;

import java.util.EnumSet;
import java.util.Map;

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
            //File f = new File("./order/target/OrderService-1.0-SNAPSHOT.jar");
            System.out.println("ShipPipeline.run() invoked, submitting job");
            controller.startJob("OrderService", "OrderService.Ship", createPipeline());

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
        ServiceFactory<?, IMap<String, Order>> materializedViewServiceFactory =
                ServiceFactories.iMapService(orderService.getView().getName());

        Pipeline p = Pipeline.create();

        StreamStage<AccountInventoryCombo> combos = p.readFrom(Sources.mapJournal(
                acctInvCombos,
                JournalInitialPosition.START_FROM_OLDEST))
                .withIngestionTimestamps()
                .setName("Read from " + acctInvCombos.getName())
                .map(Map.Entry::getValue);

        // Create ShipEvent
        StreamStage<OrderShippedEvent> shipEvents = combos.map(combo -> {
            OrderShippedEvent shipment = new OrderShippedEvent(combo.getOrderNumber());
            shipment.setItemNumber(combo.getItemNumber());
            shipment.setLocation(combo.getLocation());
            shipment.setQuantityShipped(combo.getQuantity());
            return shipment;
        });

        // Write ShipEvent to Event Store
        shipEvents.mapUsingService(eventStoreServiceFactory, (store, shipment) -> {
                    store.append(shipment);
                    return shipment;
                }).setName("Persist OrderShippedEvent to event store")

        // Update Materialized View including wait flags
        .mapUsingService(materializedViewServiceFactory, (viewMap, shipEvent) -> {
            viewMap.executeOnKey(shipEvent.getOrderNumber(), (EntryProcessor<String, Order, Order>) orderEntry -> {
                Order orderView = orderEntry.getValue();
                EnumSet<WaitingOn> waits = orderView.getWaitingOn();
                waits.remove(WaitingOn.SHIP);
                if (waits.isEmpty()) {
                    waits.add(WaitingOn.NOTHING);
                }
                System.out.println("After removing SHIP, waiting on: " + waits);
                orderEntry.setValue(orderView);
                return orderView;
            });
            return shipEvent;
        }).setName("Update Order Materialized View")
                .writeTo(Sinks.noop());
        return p;
    }
}
