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

package org.hazelcast.msfdemo.invsvc.events;

import com.hazelcast.core.HazelcastInstance;
import io.grpc.stub.StreamObserver;
import org.hazelcast.msf.eventstore.SubscriptionManager;

import java.io.Serializable;

import static org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryPulled;

public class PullInventoryEvent extends InventoryEvent implements Serializable {
    private String orderNumber = "[not provided]";
    private String locationID;
    private int quantity;
    private static SubscriptionManager<InventoryPulled> subscriptionManger;

    public synchronized static void setHazelcastInstance(HazelcastInstance hz) {
        if (subscriptionManger == null) {
            subscriptionManger = new SubscriptionManager<>(hz, InventoryPulled.getDescriptor().getFullName());
            subscriptionManger.setVerbose(false);
        }
    }

    public PullInventoryEvent() {
        super(InventoryEventTypes.PULL);
    }

    public String getLocationID() {
        return locationID;
    }

    public void setLocationID(String locationID) {
        this.locationID = locationID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public static void subscribe(StreamObserver<InventoryPulled> observer) {
        subscriptionManger.subscribe(observer, 0);
    }
    @Override
    public void publish() {
        InventoryPulled event = InventoryPulled.newBuilder()
                .setOrderNumber(orderNumber)
                .setItemNumber(getItemNumber())
                .setLocation(locationID)
                .setQuantityPulled(quantity)
                .build();
        String description = "inventory.InventoryPulled Quantity " + quantity + " @ location " + locationID + " for Order " + orderNumber;
        subscriptionManger.publish(event, description);
    }
}
