package com.hazelcast.msfdemo.ordersvc.events;

import com.hazelcast.msfdemo.ordersvc.domain.Order;

import java.io.Serializable;

public class CreateOrderEvent extends OrderEvent implements Serializable  {


    public CreateOrderEvent(String orderNumber, String acctNumber, String itemNumber, String location,
                            int quantity) {
        super(OrderEventTypes.CREATE, orderNumber, acctNumber, itemNumber, location, quantity);
        this.terminal = false;
    }

    @Override
    public Order apply(Order order) {
        order.setOrderNumber(super.orderNumber);
        order.setAcctNumber(super.accountNumber);
        order.setItemNumber(super.itemNumber);
        order.setLocation(super.location);
        order.setQuantity(super.quantity);
        order.setExtendedPrice(super.extendedPrice);
        return order;
    }
}
