package com.hazelcast.msfdemo.ordersvc.events;

public enum OrderEventTypes {
    CREATE("OrderEvent_CREATE"),
    COMPACTION("OrderEvent_COMPACTION"),
    PRICE_CALCULATED("OrderEvent_PRICED"),
    CREDIT_CHECKED("OrderEvent_CREDIT_OK"),
    INV_RESERVED("OrderEvent_INV_RESERVED"),
    COMPLETE("OrderEvent_SHIP_COMPLETE"),
    CANCELED("OrderEvent_CANCELED"), // unused
    INV_NOT_AVAIL("OrderEvent_FAIL_NO_INV"),
    CREDIT_DECLINED("OrderEvent_FAIL_CREDIT");

    private String qualifiedName;
    OrderEventTypes(String fqname) {
        qualifiedName = fqname;
    }
    public String getQualifiedName() { return qualifiedName; }
}
