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

package org.hazelcast.msfdemo.ordersvc.eventstore;

import org.hazelcast.msf.controller.MSFController;
import org.hazelcast.msf.eventstore.EventStore;
import org.hazelcast.msfdemo.ordersvc.domain.Order;
import org.hazelcast.msfdemo.ordersvc.events.CompactionEvent;
import org.hazelcast.msfdemo.ordersvc.events.OrderEvent;

public class OrderEventStore extends EventStore<Order, String, OrderEvent> {

    public static final String EVENT_STORE_NAME = "OrderEventStore";

    // Singleton implementation
    private OrderEventStore() {
        super(OrderEventStore.class.getCanonicalName(), Order::new);
        MSFController controller = MSFController.getInstance();
        String keyName = "orderNumber"; // builds an index, so case sensitive!
        eventMap = controller.createEventStore(EVENT_STORE_NAME, keyName);
    }
    private static class Singleton {
        private static final OrderEventStore INSTANCE = new OrderEventStore();
    }
    public static OrderEventStore getInstance() {
        return Singleton.INSTANCE;
    }

    // Experimental, once working abstract and move into base class
    // Callback is the state machine, driven by OrderStatus field of the OrderEvent
    // There may be an overloaded version that filters on specific state changes
    // (maybe via EnumSet)
//    public UUID registerEventListener(String orderNumber, Consumer<OrderEvent>
//            callback) {
//        UUID id = eventMap.addEntryListener(new EntryAddedListener<Long,OrderEvent>() {
//            @Override
//            public void entryAdded(EntryEvent<Long, OrderEvent> entryEvent) {
//                OrderEvent event = entryEvent.getValue();
//                callback.accept(event);
//            }
//        }, Predicates.sql("orderNumber=" + orderNumber), true);
//        return id;
//    }

//    public void removeEventListener(UUID id) {
//        eventMap.removeEntryListener(id);
//    }
//
//    public UUID registerEventHandler(String orderNumber, OrderAPIImpl.EventHandler handler) {
//
//        UUID id = eventMap.addEntryListener(new EntryAddedListener<Long,OrderEvent>() {
//            @Override
//            public void entryAdded(EntryEvent<Long, OrderEvent> entryEvent) {
//                OrderEvent event = entryEvent.getValue();
//                handler.handleEvent(event);
//            }
//        }, Predicates.sql("orderNumber=" + orderNumber), true);
//
//        // There is a lag in registering handlers - pick events we've missed
//        Set<Map.Entry<Long,OrderEvent>> missedEvents = eventMap.entrySet(Predicates.sql("orderNumber=" + orderNumber));
//        //System.out.println("Re-processing " + missedEvents.size() + " missed events");
//        for (Map.Entry<Long,OrderEvent> mapEntry : missedEvents) {
//            OrderEvent event = mapEntry.getValue();
//            if (event.getEventName().equals(OrderEventTypes.CREATE.getQualifiedName())) {
//                //System.out.println("Skipping create event");
//            } else {
//                System.out.println("Re-processing missed event (happened before handler armed)" + event);
//                handler.handleEvent(event);
//            }
//        }
//        return id;
//    }

    // Materialize method generified and moved to EventStore base class

    // Is this an all-or-nothing operation?  Maybe we want to use it for space
    // management so might set a threshold - checkpoint keys having over X entries.
    // Could also checkpoint entries older than X, but in that case we're
    // no longer append-only -- but could work around that by making a copy as
    // we go.
    public void snapshot() {
        // Get KeySet from map
        // For each key:
        //    Account account = materialize(key)
        //    Remove all entries for the key from the event store
        //    Append the snapshot record to the event store
        //    (maybe flip order of those last two so we have less risk of data loss)
    }

    public CompactionEvent writeAsCheckpoint(Order order, long sequence) {
        CompactionEvent checkpoint = new CompactionEvent(order.getOrderNumber(),
            order.getAcctNumber(), order.getItemNumber(), order.getLocation(),
                order.getQuantity(), order.getExtendedPrice());
        return checkpoint;
    }
}
