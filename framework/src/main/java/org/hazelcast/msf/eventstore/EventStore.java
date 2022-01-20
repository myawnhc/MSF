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

package org.hazelcast.msf.eventstore;

import com.hazelcast.cp.IAtomicLong;
import com.hazelcast.map.IMap;
import com.hazelcast.query.Predicates;
import org.hazelcast.msf.controller.MSFController;
import org.hazelcast.msf.persistence.DTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/** Hazelcast-centric implementation of an Event Store to support the Event Sourcing
 *  microservice pattern (@see https://microservices.io/patterns/data/event-sourcing.html)
 *  An 'event' is a representation of state changes made to the domain object during the
 *  execution of business logic.  Rather than persisting the current, up-to-date view of
 *  a domain object, in Event Sourcing we instead persist the sequence of state changes
 *  over the history of the object, allowing the current state to be materialized
 *  on demand.  (In the MSF implementation, we always keep a materialized view built
 *  to support querying, a la the CQRS pattern.
 *
 *  Although the event log is logically an append-only store, it is implemented as
 *  a Hazelcast IMap, with the key being a sequence number and a sorted index
 *  maintained on the item key + sequence compound item.
 *
 * @param <D> the Domain object which is updated by the event sequence
 * @param <K> the type of the key of the domain object
 * @param <T> the Event Object type that will be appended to the Event Store
 */
public abstract class EventStore<D extends DTO<K>, K, T extends SequencedEvent> {
    // Key is sequence number; event store is not kept sorted so must sort upon use
    protected IMap<Long, T> eventMap;
    protected IAtomicLong sequenceProvider;

    // Domain object (e.g., Account, not AccountEvent or AccountEventStore) has to
    // be constructed in the materialize method, so we pass in a constructor
    // when creating the store.  (e.g., new EventStore("aes", Account::new) )
    protected final Supplier<? extends D> domainObjectConstructor;

    /** Constructs an event store
     * @param storeName  the name for the event store
     * @param domainObjectConstructor a zero-argument constructor that can be used to
     *                                create domain objects during materialization
     */
    public EventStore(String storeName, Supplier<? extends D> domainObjectConstructor) {
        // If we have an uninitialized controller, it means we're in C/S mode ... so hard-coded false is OK here
        sequenceProvider = MSFController.getInstance().getSequenceGenerator(storeName);
        this.domainObjectConstructor = Objects.requireNonNull(domainObjectConstructor);
    }

    private Long getNextSequence() {
        return sequenceProvider.incrementAndGet();
    }

    /** Appends an Event object to the event store */
    public void append(T event) {
        Long sequence = getNextSequence();
        eventMap.set(sequence, event);
        event.publish();
    }

    /** Materialize a domain object from the event store.  In normal operation this isn't
     * used as we always keep an up-to-date materialized view, but in a recovery
     * scenario where the in-memory copy is lost this will rebuild it.
     *
     * @param predicate A SQL format 'where' clause that selects the desired objects
     *                  from the event store.  Normally this would be in the form
     *                  {keyname}={value}, but for special cases it could specify
     *                  and condition.  Conditions that cause events for multiple
     *                  keys to be returned are not likely to produce useful results.
     * @return a domain object reflecting all Events
     */
    public D materialize(String predicate) {
        D materializedObject = domainObjectConstructor.get();
        List<Long> keys = new ArrayList(eventMap.keySet(Predicates.sql(predicate)));
        Collections.sort(keys);
        for (Long sequence : keys) {
            T accountEvent = eventMap.get(sequence);
            accountEvent.apply(materializedObject);
        }
        return materializedObject;
    }

    public void compact(String predicate, float compressionPercentage) {
        // Note this shares a lot of code with materialize, should eventually
        // refactor to eliminate duplication
        D compressedData = domainObjectConstructor.get();
        List<Long> keys = new ArrayList(eventMap.keySet(Predicates.sql(predicate)));
        Collections.sort(keys);
        int entriesToCompress = (int) (keys.size() * compressionPercentage);
        System.out.println("Will compress " + entriesToCompress + " of " + keys.size() + " entries");
        long sequenceOfLastAppliedEvent = -1;
        for (int i=0; i<entriesToCompress; i++) {
            T accountEvent = eventMap.get(keys.get(i));
            accountEvent.apply(compressedData);
            eventMap.remove(accountEvent);
            sequenceOfLastAppliedEvent = keys.get(i);
        }
        // Now write the summarized object back into the slot of the last-compressed entry
        T checkpointEvent = (T) writeAsCheckpoint(compressedData, sequenceOfLastAppliedEvent);
        eventMap.put(sequenceOfLastAppliedEvent, checkpointEvent);
    }

    abstract public SequencedEvent writeAsCheckpoint(D domainObject, long sequence);

    /** Return the event map.  This is public to support unit tests; framework users
     * should not need to call this directly.
     *
     * @return
     */
    public IMap<Long, T> getEventMap() {
        return eventMap;
    }
}
