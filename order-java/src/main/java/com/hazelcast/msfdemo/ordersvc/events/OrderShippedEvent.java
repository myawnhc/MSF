package com.hazelcast.msfdemo.ordersvc.events;

import com.hazelcast.msfdemo.ordersvc.domain.Order;

import java.io.Serializable;

public class OrderShippedEvent extends OrderEvent implements Serializable {

    public OrderShippedEvent(String orderNumber, String acctNumber, String itemNumber, String location,
                            int quantity, int extendedPrice) {
        super(OrderEventTypes.COMPLETE, orderNumber, acctNumber, itemNumber, location, quantity);
        this.extendedPrice = extendedPrice;
        this.terminal = true;
    }

    @Override
    public Order apply(Order order) {
        order.setOrderNumber(super.orderNumber);
        order.setAcctNumber(super.accountNumber);
        order.setItemNumber(super.itemNumber);
        order.setLocation(super.location);
        order.setQuantity(super.quantity);
        order.setExtendedPrice(super.extendedPrice);
        return order;    }
}
