/*
 * Copyright 2018-2021 Hazelcast, Inc
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.package com.theyawns.controller.launcher;
 *
 */

package org.hazelcast.msfdemo.ordersvc.events;

import org.hazelcast.msfdemo.ordersvc.domain.Order;

import java.io.Serializable;
import java.util.function.UnaryOperator;

public class CompactionEvent extends OrderEvent implements Serializable,
        UnaryOperator<Order> {

    private final String accountNumber;
    private final String itemNumber;
    private final int    quantity;
    private final String location;
    private final int    extendedPrice;

    public CompactionEvent(String orderNumber, String acctNumber, String itemNumber,
                           String location, int quantity, int price) {
        super(orderNumber);
        this.accountNumber = acctNumber;
        this.itemNumber = itemNumber;
        this.location = location;
        this.quantity = quantity;
        this.extendedPrice = price;
    }

    public String toString() {
        return "COMPACT order " + orderNumber;
    }

    @Override    // Abstract method from SequencedEvent
    public void publish() {
        System.out.println("No implementation for CompactionEvent.publish in order service");
    }

    @Override // UnaryOperator<Account>
    public Order apply(Order order) {
        // Potentially changes everything except the account number
        order.setAcctNumber(accountNumber);
        order.setItemNumber(itemNumber);
        order.setLocation(location);
        order.setQuantity(quantity);
        order.setExtendedPrice(extendedPrice);
        return order;
    }
}
