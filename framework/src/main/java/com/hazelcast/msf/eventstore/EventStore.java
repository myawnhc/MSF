package com.hazelcast.msf.eventstore;

import com.hazelcast.cp.IAtomicLong;
import com.hazelcast.map.IMap;
import com.hazelcast.msf.controller.MSFController;
import com.hazelcast.msf.persistence.DTO;
import com.hazelcast.query.Predicates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

// TODO:  if materialize doesn't abstract well, can drop DTO<K> and K parameters.
public class EventStore<D extends DTO<K>, K, T extends SequencedEvent> {
    // Key is sequence number; event store is not kept sorted so must sort upon use
    protected IMap<Long, T> eventMap;
    protected IAtomicLong sequenceProvider;

    // Domain object (e.g., Account, not AccountEvent or AccountEventStore) has to
    // be constructed in the materialize method, so  we pass in a constructor
    // when creating the store.  (e.g., new EventStore("aes", Account::new) )
    protected final Supplier<? extends D> domainObjectConstructor;

    public EventStore(String storeName, Supplier<? extends D> domainObjectConstructor) {
        sequenceProvider = MSFController.getInstance().getSequenceGenerator(storeName);
        this.domainObjectConstructor = Objects.requireNonNull(domainObjectConstructor);
    }

    // Should be able to take this private again with latest change to jet ServiceProvider
    private Long getNextSequence() {
        return sequenceProvider.incrementAndGet();
    }

    // Not used by Jet pipeline which gets map reference and Sinks into it
    public void append(T event) {
        Long sequence = getNextSequence();
        eventMap.set(sequence, event);
    }

    // Build a materialized view from the Event Store.  Should not be necessary
    // in normal operation as we do this on-the-fly, but if we are in recovery
    // scenario or taking a snapshot, then we do this.
    public D materialize(String predicate) {
        D materializedObject = domainObjectConstructor.get();
        List<Long> keys = new ArrayList(eventMap.keySet(Predicates.sql(predicate)));
        Collections.sort(keys);
        for (Long sequence : keys) {
            T accountEvent = eventMap.get(sequence);
            // TODO: apply comes from UnaryOperator on AccountEvent, would need to move it
            // up to SequencedEvent and parameterize it
            accountEvent.apply(materializedObject);
        }
        return materializedObject;
    }

    // Used by unit tests, otherwise could be private
    public IMap<Long, T> getEventMap() {
        return eventMap;
    }
}
