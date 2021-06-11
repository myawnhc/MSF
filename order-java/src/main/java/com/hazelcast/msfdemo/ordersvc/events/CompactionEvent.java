package com.hazelcast.msfdemo.ordersvc.events;

import com.hazelcast.msfdemo.ordersvc.domain.Order;

import java.io.Serializable;
import java.util.function.UnaryOperator;

public class CompactionEvent extends OrderEvent implements Serializable,
        UnaryOperator<Order> {

    public CompactionEvent(String orderNumber, String acctNumber, String itemNumber,
                           String location, int quantity, int price) {
        super(OrderEventTypes.COMPACTION, orderNumber, acctNumber, itemNumber,
                location, quantity);
        super.extendedPrice = price;
    }

    public String toString() {
        return "COMPACT order " + orderNumber;
    }

    @Override // UnaryOperator<Account>
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
