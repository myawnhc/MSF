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

package com.hazelcast.msfdemo.ordersvc.eventstore;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.msf.controller.MSFController;
import com.hazelcast.msf.eventstore.EventStore;
import com.hazelcast.msfdemo.ordersvc.business.OrderAPIImpl;
import com.hazelcast.msfdemo.ordersvc.domain.Order;
import com.hazelcast.msfdemo.ordersvc.events.CompactionEvent;
import com.hazelcast.msfdemo.ordersvc.events.OrderEvent;
import com.hazelcast.msfdemo.ordersvc.events.OrderEventTypes;
import com.hazelcast.msfdemo.ordersvc.events.OrderShippedEvent;
import com.hazelcast.query.Predicates;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

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
    public UUID registerEventListener(String orderNumber, Consumer<OrderEvent>
            callback) {
        UUID id = eventMap.addEntryListener(new EntryAddedListener<Long,OrderEvent>() {
            @Override
            public void entryAdded(EntryEvent<Long, OrderEvent> entryEvent) {
                OrderEvent event = entryEvent.getValue();
                callback.accept(event);
            }
        }, Predicates.sql("orderNumber=" + orderNumber), true);
        return id;
    }

    public void removeEventListener(UUID id) {
        eventMap.removeEntryListener(id);
    }

    public UUID registerEventHandler(String orderNumber, OrderAPIImpl.EventHandler handler) {

        UUID id = eventMap.addEntryListener(new EntryAddedListener<Long,OrderEvent>() {
            @Override
            public void entryAdded(EntryEvent<Long, OrderEvent> entryEvent) {
                OrderEvent event = entryEvent.getValue();
                handler.handleEvent(event);
            }
        }, Predicates.sql("orderNumber=" + orderNumber), true);

        // There is a lag in registering handlers - pick events we've missed
        Set<Map.Entry<Long,OrderEvent>> missedEvents = eventMap.entrySet(Predicates.sql("orderNumber=" + orderNumber));
        System.out.println("Re-processing " + missedEvents.size() + " missed events");
        for (Map.Entry<Long,OrderEvent> mapEntry : missedEvents) {
            OrderEvent event = mapEntry.getValue();
            if (event.getEventName().equals(OrderEventTypes.CREATE.getQualifiedName())) {
                System.out.println("Skipping create event");
            } else {
                handler.handleEvent(event);
            }
        }

        // TODO: remove this fake event code!
        try {
            // Wait long enough for price lookup to clear, otherwise we close stream prematurely
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        OrderShippedEvent ship = new OrderShippedEvent(orderNumber, "AcctNum", "ItemNum", "Loc",
                10, 1000);
        super.append(ship);
        // TODO: end fake event code
        return id;
    }

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
