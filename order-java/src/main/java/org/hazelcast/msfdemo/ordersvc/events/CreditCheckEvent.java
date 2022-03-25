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

import com.hazelcast.core.HazelcastInstance;
import io.grpc.stub.StreamObserver;
import org.hazelcast.msf.eventstore.SubscriptionManager;
import org.hazelcast.msfdemo.ordersvc.domain.Order;
import org.hazelcast.msfdemo.ordersvc.domain.WaitingOn;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.function.UnaryOperator;

import static org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreditChecked;

public class CreditCheckEvent extends OrderEvent implements Serializable, UnaryOperator<Order> {

    private String accountNumber;
    private int amountRequested;
    private boolean sufficient;

    private static SubscriptionManager<OrderOuterClass.CreditChecked> subscriptionManager;

    // Called from pipeline that creates the events
    public synchronized static void setHazelcastInstance(HazelcastInstance hz) {
        if (subscriptionManager == null) {
            subscriptionManager = new SubscriptionManager<>(hz, CreditChecked.getDescriptor().getFullName());
            subscriptionManager.setVerbose(false);
        }
    }

    public CreditCheckEvent(String orderNumber) {
        super(orderNumber);
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public int getAmountRequested() {
        return amountRequested;
    }

    public void setAmountRequested(int amountRequested) {
        this.amountRequested = amountRequested;
    }

    public boolean isSufficient() {
        return sufficient;
    }

    public void setSufficient(boolean sufficient) {
        this.sufficient = sufficient;
    }

    public static void subscribe(StreamObserver<OrderOuterClass.CreditChecked> observer) {
        subscriptionManager.subscribe(observer, 0);
    }

    @Override
    public void publish() {
        OrderOuterClass.CreditChecked event = OrderOuterClass.CreditChecked.newBuilder()
                .setOrderNumber(orderNumber)
                .setAccountNumber(accountNumber)
                .setAmountRequested(amountRequested)
                .setApproved(sufficient)
                .build();
        subscriptionManager.publish(event);
    }

    @Override
    public Order apply(Order order) {
        EnumSet<WaitingOn> waits = order.getWaitingOn();
        waits.remove(WaitingOn.CREDIT_CHECK);
        if (waits.isEmpty()) {
            waits.add(WaitingOn.CHARGE_ACCOUNT);
            waits.add(WaitingOn.PULL_INVENTORY);
        }
        return order;
    }
}
