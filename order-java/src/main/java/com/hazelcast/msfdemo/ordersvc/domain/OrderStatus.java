package com.hazelcast.msfdemo.ordersvc.domain;

public enum OrderStatus {
    NEW("New"),
    CHECKING_PRICE("Checking price"),
    CHECKING_CREDIT("Checking credit"),
    RESERVE_INVENTORY("Reserving inventory"),
    CHARGE_ACCOUNT("Charging account"),
    PULLING_INVENTORY("Pulling inventory"),
    COMPLETE("Complete/shipped"),
    UNABLE_TO_COMPLETE("Unable to complete");

    String description;
    OrderStatus(String description) {
        this.description = description;
    }
}
