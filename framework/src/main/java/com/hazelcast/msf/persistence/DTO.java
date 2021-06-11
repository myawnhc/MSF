package com.hazelcast.msf.persistence;

/** Data Transfer Object base class; parameterized by the Key type */
public abstract class DTO<K> {
    private K key;

    public K getKey() { return key; }
}
