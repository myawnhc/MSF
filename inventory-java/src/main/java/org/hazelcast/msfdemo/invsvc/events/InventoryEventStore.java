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

import com.hazelcast.core.HazelcastInstance;
import org.hazelcast.msf.eventstore.EventStore;
import org.hazelcast.msfdemo.invsvc.domain.Item;


public class InventoryEventStore extends EventStore<Item, String, InventoryEvent> {

    private static final String mapName = "InventoryEventStore";
    private static final String keyName = "itemNumber";

    public InventoryEventStore(HazelcastInstance hazelcast) {
        super(mapName, keyName, Item::new, hazelcast);
        // NOTE: if more events are added, they need to be initialized here as well
        PullInventoryEvent.setHazelcastInstance(hazelcast);
        ReserveInventoryEvent.setHazelcastInstance(hazelcast);
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

    public CompactionEvent writeAsCheckpoint(Item item, long sequence) {
        CompactionEvent checkpoint = null;
//                new CompactionEvent(item.getOrderNumber(),
//                item.getAcctNumber(), item.getItemNumber(), item.getLocation(),
//                item.getQuantity(), item.getExtendedPrice());
        return checkpoint;
    }
}
