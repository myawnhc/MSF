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

package org.hazelcast.msfdemo.invsvc.events;

import org.hazelcast.msf.controller.MSFController;
import org.hazelcast.msf.eventstore.EventStore;
import org.hazelcast.msfdemo.invsvc.domain.Item;


public class InventoryEventStore extends EventStore<Item, String, InventoryEvent> {

    public static final String EVENT_STORE_NAME = "InventoryEventStore";

    // Singleton implementation
    private InventoryEventStore() {
        super(InventoryEventStore.class.getCanonicalName(), Item::new);
        MSFController controller = MSFController.getInstance();
        String keyName = "itemNumber"; // builds an index, so case sensitive!
        eventMap = controller.createEventStore(EVENT_STORE_NAME, keyName);
    }

    private static class Singleton {
        private static final InventoryEventStore INSTANCE = new InventoryEventStore();
    }

    public static InventoryEventStore getInstance() {
        return Singleton.INSTANCE;
    }


//    public void removeEventListener(UUID id) {
//        eventMap.removeEntryListener(id);
//    }
//
//    public UUID registerEventHandler(String orderNumber, InventoryAPIImpl.EventHandler handler) {
//
//        UUID id = eventMap.addEntryListener(new EntryAddedListener<Long,InventoryEvent>() {
//            @Override
//            public void entryAdded(EntryEvent<Long, InventoryEvent> entryEvent) {
//                InventoryEvent event = entryEvent.getValue();
//                handler.handleEvent(event);
//            }
//        }, Predicates.sql("orderNumber=" + orderNumber), true);
//
//        // There is a lag in registering handlers - pick events we've missed
//        Set<Map.Entry<Long,InventoryEvent>> missedEvents = eventMap.entrySet(Predicates.sql("orderNumber=" + orderNumber));
//        //System.out.println("Re-processing " + missedEvents.size() + " missed events");
//        for (Map.Entry<Long,InventoryEvent> mapEntry : missedEvents) {
//            InventoryEvent event = mapEntry.getValue();
//            if (event.getEventName().equals(OrderEventTypes.CREATE.getQualifiedName())) {
//                //System.out.println("Skipping create event");
//            } else {
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

    public CompactionEvent writeAsCheckpoint(Item item, long sequence) {
        CompactionEvent checkpoint = null;
//                new CompactionEvent(item.getOrderNumber(),
//                item.getAcctNumber(), item.getItemNumber(), item.getLocation(),
//                item.getQuantity(), item.getExtendedPrice());
        return checkpoint;
    }
}
