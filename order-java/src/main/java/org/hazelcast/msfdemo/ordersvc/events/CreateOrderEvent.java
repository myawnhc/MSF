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

import io.grpc.stub.StreamObserver;
import org.hazelcast.msf.eventstore.SubscriptionManager;
import org.hazelcast.msfdemo.ordersvc.domain.Order;
import org.hazelcast.msfdemo.ordersvc.domain.WaitingOn;
import org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderCreated;

import java.io.Serializable;
import java.util.EnumSet;

public class CreateOrderEvent extends OrderEvent implements Serializable  {

    private String accountNumber;
    private String itemNumber;
    private int    quantity;
    private String location;

    static SubscriptionManager<OrderCreated> subscriptionManager = new SubscriptionManager<>(OrderCreated.getDescriptor().getFullName());

    public CreateOrderEvent(String orderNumber, String acctNumber, String itemNumber, String location,
                            int quantity) {
        super(orderNumber);
        this.accountNumber = acctNumber;
        this.itemNumber = itemNumber;
        this.location = location;
        this.quantity = quantity;
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

    public static void subscribe(StreamObserver<OrderCreated> observer) {
        subscriptionManager.subscribe(observer, 0);
    }

    public void publish() {
        OrderCreated grpcEvent = OrderCreated.newBuilder()
                .setOrderNumber(this.orderNumber)
                .setItemNumber(this.itemNumber)
                .setAccountNumber(this.accountNumber)
                .setQuantity(this.quantity)
                .setLocation(this.location)
                .build();
        subscriptionManager.publish(grpcEvent);
    }

    @Override
    public Order apply(Order order) {
        order.setOrderNumber(super.orderNumber);
        order.setAcctNumber(accountNumber);
        order.setItemNumber(itemNumber);
        order.setLocation(location);
        order.setQuantity(quantity);
        order.setWaitingOn(EnumSet.of(WaitingOn.PRICE_LOOKUP));
        return order;
    }
}
