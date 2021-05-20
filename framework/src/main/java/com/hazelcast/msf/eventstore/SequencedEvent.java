package com.hazelcast.msf.eventstore;

public class SequencedEvent {

    private long sequence;

    public void setSequence(Long sequence) {
        this.sequence =  sequence;
    }
    public long getSequence() { return sequence; }

}
