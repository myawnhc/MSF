package com.hazelcast.msfdemo.acctsvc.events;

public enum AccountEventTypes {
    OPEN("AccountEvent_OPEN"),
    ADJUST( "AccountEvent_ADJUST"),
    TRANSFER("AccountEvent_TRANSFER"),
    COMPACTION("AccountEvent_COMPACTION");

    private String qualifiedName;
    AccountEventTypes(String fqname) {
        qualifiedName = fqname;
    }
    public String getQualifiedName() { return qualifiedName; }
}
