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

import static com.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderPriced;

public class PriceLookupEvent extends OrderEvent implements Serializable {

    private int extendedPrice;
    private String itemNumber;
    private String location;
    private int quantity;

    private static SubscriptionManager<OrderPriced> subscriptionManager = new SubscriptionManager<>(OrderPriced.getDescriptor().getFullName());

    public PriceLookupEvent(String orderNumber, String itemNumber, String location, int quantity, int price) {
        super(orderNumber);
        this.itemNumber = itemNumber;
        extendedPrice = price;
        this.location = location;
        this.quantity = quantity;
    }

    public void setExtendedPrice(int price) { this.extendedPrice = price; }
    public int getExtendedPrice() { return extendedPrice; }

    public static void subscribe(StreamObserver<OrderPriced> observer) {
        subscriptionManager.subscribe(observer, 0);
    }
    @Override
    public void publish() {
        OrderPriced event = OrderPriced.newBuilder()
                .setOrderNumber(orderNumber)
                .setExtendedPrice(extendedPrice)
                .build();
        String description = "OrderPriced: Order " + orderNumber + " ExtendedPrice: " + extendedPrice;
        subscriptionManager.publish(event, description);
    }

    @Override
    public Order apply(Order order) {
        order.setOrderNumber(super.orderNumber);
        order.setItemNumber(itemNumber);
        order.setLocation(location);
        order.setQuantity(quantity);
        order.setExtendedPrice(extendedPrice);
        order.setWaitingOn(EnumSet.of(WaitingOn.CREDIT_CHECK, WaitingOn.RESERVE_INVENTORY));
        return order;
    }
}
