package com.hazelcast.msfdemo.acctsvc.events;

public enum AccountEventTypes {
    OPEN("AccountEvent_OPEN"),
    ADJUST( "AccountEvent_ADJUST"),
    TRANSFER("AccountEvent_TRANSFER"),
    SNAPSHOT("AccountEvent_SNAPSHOT");

    private String qualifiedName;
    AccountEventTypes(String fqname) {
        qualifiedName = fqname;
    }
    public String getQualifiedName() { return qualifiedName; }
}
