/*
 * Copyright 2018-2022 Hazelcast, Inc
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
 *  limitations under the License.
 */

package org.hazelcast.msfdemo.ordersvc.events;

import io.grpc.stub.StreamObserver;
import org.hazelcast.msf.eventstore.SubscriptionManager;
import org.hazelcast.msfdemo.ordersvc.domain.Order;
import org.hazelcast.msfdemo.ordersvc.domain.WaitingOn;

import java.io.Serializable;
import java.util.EnumSet;

public class InventoryReserveEvent extends OrderEvent implements Serializable {

    private String accountNumber;
    private String itemNumber;
    private int quantity;
    private String location;
    private String failureReason;
    private static final SubscriptionManager<OrderOuterClass.InventoryReserved> subscriptionManager = new SubscriptionManager<>(OrderOuterClass.InventoryReserved.getDescriptor().getFullName());

    public InventoryReserveEvent(String orderNumber) {
        super(orderNumber);
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public static void subscribe(StreamObserver<OrderOuterClass.InventoryReserved> observer) {
        subscriptionManager.subscribe(observer, 0);
    }

    @Override
    public void publish() {
        OrderOuterClass.InventoryReserved event = OrderOuterClass.InventoryReserved.newBuilder()
                .setOrderNumber(orderNumber)
                .setQuantityReserved(quantity)
                .build();
        String description = "order.InventoryReserved Quantity: " + quantity + " for Order: " + orderNumber;
        subscriptionManager.publish(event, description);
    }


    public Order apply(Order order) {
        // May in some future implementation alter quantity, for partial ship,
        // but we're not doing that currently.
        order.setQuantity(quantity);
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


