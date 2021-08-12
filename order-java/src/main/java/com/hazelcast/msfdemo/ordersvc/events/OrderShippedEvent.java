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

import com.hazelcast.msf.eventstore.SubscriptionManager;
import com.hazelcast.msfdemo.ordersvc.domain.Order;
import com.hazelcast.msfdemo.ordersvc.domain.WaitingOn;
import io.grpc.stub.StreamObserver;

import java.io.Serializable;
import java.util.EnumSet;

public class OrderShippedEvent extends OrderEvent implements Serializable {

    private int quantityShipped;
    private String itemNumber;
    private String location;

    static SubscriptionManager<OrderOuterClass.OrderShipped> subscriptionManager = new SubscriptionManager<>(OrderOuterClass.OrderShipped.getDescriptor().getFullName());

    public OrderShippedEvent(String orderNumber) {
        super(orderNumber);
    }

    public int getQuantityShipped() {
        return quantityShipped;
    }

    public void setQuantityShipped(int quantityShipped) {
        this.quantityShipped = quantityShipped;
    }

    public String getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public static void subscribe(StreamObserver<OrderOuterClass.OrderShipped> observer) {
        subscriptionManager.subscribe(observer, 0);
    }

    @Override
    public void publish() {
        OrderOuterClass.OrderShipped grpcEvent = OrderOuterClass.OrderShipped.newBuilder()
                .setOrderNumber(this.orderNumber)
                .setItemNumber(this.itemNumber)
                .setQuantityShipped(this.quantityShipped)
                .build();
        subscriptionManager.publish(grpcEvent);
    }

    @Override
    public Order apply(Order order) {
        order.setOrderNumber(super.orderNumber);
        order.setItemNumber(itemNumber);
        order.setLocation(location);
        order.setQuantity(quantityShipped);
        order.setWaitingOn(EnumSet.of(WaitingOn.NOTHING));
        return order;    }
}
