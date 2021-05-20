package com.hazelcast.msf.eventstore;

import com.hazelcast.cp.IAtomicLong;
import com.hazelcast.map.IMap;
import com.hazelcast.msf.controller.MSFController;

public class EventStore<T extends SequencedEvent> {
    // Key is timestamp (actually sequence number)
    protected IMap<Long, T> eventMap;
    protected IAtomicLong sequenceProvider;

    public EventStore(String storeName) {
        sequenceProvider = MSFController.getInstance().getSequenceGenerator(storeName);
    }

    public Long getNextSequence() {
        return sequenceProvider.incrementAndGet();
    }

    // Not used by Jet pipeline which gets map reference and Sinks into it
    public void append(T event) {
        Long sequence = getNextSequence();
        eventMap.set(sequence, event);
    }

    // We must expose internal implementation to Jet since it won't call
    // our append method (unless we serialize this entire class as part
    // of the map stage, which seems wasteful)
    public IMap<Long, T> getEventMap() {
        return eventMap;
    }
}
