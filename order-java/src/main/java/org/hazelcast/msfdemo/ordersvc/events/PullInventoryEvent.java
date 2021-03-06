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

import static org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.InventoryPulled;

public class PullInventoryEvent extends OrderEvent implements Serializable, UnaryOperator<Order> {

    private String itemNumber;
    private String location;
    private int quantityPulled;

    private static SubscriptionManager<InventoryPulled> subscriptionManager;

    // Called from pipeline that creates the events
    public synchronized static void setHazelcastInstance(HazelcastInstance hz) {
        if (subscriptionManager == null) {
            subscriptionManager = new SubscriptionManager<>(hz, InventoryPulled.getDescriptor().getFullName());
            subscriptionManager.setVerbose(false);
        }
    }

    public PullInventoryEvent(String orderNumber) {
        super(orderNumber);
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

    public int getQuantityPulled() {
        return quantityPulled;
    }

    public void setQuantityPulled(int quantityPulled) {
        this.quantityPulled = quantityPulled;
    }

    public static void subscribe(StreamObserver<OrderOuterClass.InventoryPulled> observer) {
        subscriptionManager.subscribe(observer, 0);
    }

    @Override
    public void publish() {
        OrderOuterClass.InventoryPulled event = OrderOuterClass.InventoryPulled.newBuilder()
                .setOrderNumber(orderNumber)
                .setItemNumber(itemNumber)
                .setLocation(location)
                .setQuantity(quantityPulled)
                .build();
        subscriptionManager.publish(event);
    }

    @Override
    public Order apply(Order order) {
        order.setQuantity(quantityPulled);
        EnumSet<WaitingOn> waits = order.getWaitingOn();
        waits.remove(WaitingOn.PULL_INVENTORY);
        if (waits.isEmpty()) {
            waits.add(WaitingOn.SHIP);
        }
        return order;
    }
}
