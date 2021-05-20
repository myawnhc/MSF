package com.hazelcast.msf.persistence;

// Probably only need for this is to specify the key type and getter method for it
// Everything else will be in the concrete subclass
public abstract class DTO<K> {
    K key;

    public K getKey() { return key; }

}
