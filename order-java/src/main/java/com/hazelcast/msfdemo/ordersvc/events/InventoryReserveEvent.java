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

public class InventoryReserveEvent extends OrderEvent implements Serializable {

    private String failureReason;

    public InventoryReserveEvent() {
        super(OrderEventTypes.INV_RESERVED);
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public Order apply(Order order) {
        order.setOrderNumber(super.orderNumber);
        order.setAcctNumber(super.accountNumber);
        order.setItemNumber(super.itemNumber);
        order.setLocation(super.location);
        order.setQuantity(super.quantity);
        order.setExtendedPrice(super.extendedPrice);
        EnumSet<WaitingOn> waits = order.getWaitingOn();
        waits.remove(WaitingOn.RESERVE_INVENTORY);
        if (waits.isEmpty()) {
            waits.add(WaitingOn.CHARGE_ACCOUNT);
            waits.add(WaitingOn.PULL_INVENTORY);
        }
        return order;
    }

    @Override
    public String toString() {
        return super.toString() + " " + (failureReason == null ? "OK" : failureReason);
    }
}
