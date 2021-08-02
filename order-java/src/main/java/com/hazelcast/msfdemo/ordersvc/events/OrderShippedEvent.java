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
import com.hazelcast.msfdemo.ordersvc.domain.WaitingOn;

import java.io.Serializable;
import java.util.EnumSet;

public class OrderShippedEvent extends OrderEvent implements Serializable {

    private int quantityShipped;
    private String itemNumber;

    public OrderShippedEvent(String orderNumber, String itemNumber, String location,
                            int quantity, int extendedPrice) {
        super(orderNumber);
        this.quantityShipped = quantity;
    }

    @Override
    public void publish() {
        System.out.println("****** OrderShippedEvent.publish unimplemented!");
    }

    @Override
    public Order apply(Order order) {
        order.setOrderNumber(super.orderNumber);
        order.setItemNumber(itemNumber);
        order.setQuantity(quantityShipped);
        order.setWaitingOn(EnumSet.of(WaitingOn.NOTHING));
        return order;    }
}
