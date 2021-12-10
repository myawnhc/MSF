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

import java.io.Serializable;
import java.util.EnumSet;
import java.util.function.UnaryOperator;

public class ChargeAccountEvent extends OrderEvent implements Serializable, UnaryOperator<Order> {

    private String accountNumber;
    private int amountCharged;

    private static final SubscriptionManager<OrderOuterClass.AccountCharged> subscriptionManager = new SubscriptionManager<>(OrderOuterClass.AccountCharged.getDescriptor().getFullName());

    public ChargeAccountEvent(String orderNumber) {
        super(orderNumber);
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public int getAmountRequested() {
        return amountCharged;
    }

    public void setAmountRequested(int amountRequested) {
        this.amountCharged = amountRequested;
    }

    public static void subscribe(StreamObserver<OrderOuterClass.AccountCharged> observer) {
        subscriptionManager.subscribe(observer, 0);
    }

    @Override
    public void publish() {
        OrderOuterClass.AccountCharged event = OrderOuterClass.AccountCharged.newBuilder()
                .setOrderNumber(orderNumber)
                .setAccountNumber(accountNumber)
                .setAmountCharged(amountCharged)
                .build();
        subscriptionManager.publish(event);
    }

    @Override
    public Order apply(Order order) {
        EnumSet<WaitingOn> waits = order.getWaitingOn();
        waits.remove(WaitingOn.CHARGE_ACCOUNT);
        if (waits.isEmpty()) {
            waits.add(WaitingOn.SHIP);
        }
        return order;
    }
}
