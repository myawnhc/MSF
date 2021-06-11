package com.hazelcast.msfdemo.ordersvc.views;

import com.hazelcast.msf.persistence.DAO;
import com.hazelcast.msfdemo.ordersvc.domain.Order;

public class OrderDAO extends DAO<Order, String> {

    public OrderDAO() {
        super("order");
    }

    // Non-inheritable query methods

}
