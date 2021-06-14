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

public class PriceLookupEvent extends OrderEvent implements Serializable {

    public PriceLookupEvent(String orderNumber, String accountNumber, String itemNumber, String location, int quantity, int price) {
        super(OrderEventTypes.PRICE_CALCULATED, orderNumber, accountNumber, itemNumber, location, quantity);
        super.extendedPrice = price;
    }

    public void setExtendedPrice(int price) { this.extendedPrice = price; }

    @Override
    public Order apply(Order order) {
        order.setOrderNumber(super.orderNumber);
        order.setAcctNumber(super.accountNumber);
        order.setItemNumber(super.itemNumber);
        order.setLocation(super.location);
        order.setQuantity(super.quantity);
        order.setExtendedPrice(super.extendedPrice);
        order.setWaitingOn(EnumSet.of(WaitingOn.CREDIT_CHECK, WaitingOn.RESERVE_INVENTORY));
        return order;
    }
}
