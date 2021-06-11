package com.hazelcast.msfdemo.ordersvc.events;

import com.hazelcast.msf.eventstore.SequencedEvent;
import com.hazelcast.msfdemo.ordersvc.domain.Order;

import java.io.Serializable;

public abstract class OrderEvent extends SequencedEvent<Order> implements Serializable {

    protected OrderEventTypes eventType;
    protected String orderNumber;
    protected String accountNumber;
    protected String itemNumber;
    protected int    quantity;
    protected String location;
    protected int    extendedPrice;
    protected boolean terminal;

    public OrderEvent(OrderEventTypes type, String orderNumber, String accountNumber,
                      String itemNumber, String location, int quantity) {
        this.eventType = type;
        this.orderNumber = orderNumber;
        this.accountNumber = accountNumber;
        this.itemNumber = itemNumber;
        this.location = location;
        this.quantity = quantity;
    }

    public String getOrderNumber() { return orderNumber; }
    public String getAccountNumber() { return accountNumber; }
    public String getItemNumber() { return itemNumber; }
    public String getLocation() { return location; }
    public int getQuantity() { return quantity; }
    public int getExtendedPrice() { return extendedPrice; }
    public String getEventName() { return eventType.getQualifiedName(); }
    public boolean isTerminal() { return terminal; }

    @Override
    public String toString() {
        return eventType.getQualifiedName() + " O:" + orderNumber + " A:" + accountNumber +
                " I: " + itemNumber + " L: " + location +
                " Q: " + quantity + " $: " + extendedPrice + " TERM?: " + isTerminal();
    }
}
