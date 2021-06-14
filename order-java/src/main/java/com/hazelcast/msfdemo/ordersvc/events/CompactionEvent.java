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
