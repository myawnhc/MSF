package com.hazelcast.msf.eventstore;

import java.util.function.UnaryOperator;

// T is type of the domain object for the event being sequenced, used to materialize T from the event store
// concrete subclass must implement apply(T) for materialization
public abstract class SequencedEvent<T> implements UnaryOperator<T> {

    // Now that all sequencing is back in the EventStore rather than here,
    // we could could rename this, but Event is so overloaded I'd rather
    // not add to the confusion.

//    private long sequence;

//    public void setSequence(Long sequence) {
//        this.sequence =  sequence;
//    }
//    public long getSequence() { return sequence; }

}
