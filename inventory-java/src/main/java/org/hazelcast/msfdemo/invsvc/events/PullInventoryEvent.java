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

package org.hazelcast.msfdemo.invsvc.events;

import io.grpc.stub.StreamObserver;
import org.hazelcast.msf.eventstore.SubscriptionManager;

import java.io.Serializable;

// TODO: this is a Clone of Reserve event, update as appropriate
public class PullInventoryEvent extends InventoryEvent implements Serializable {
    private String orderNumber = "[not provided]";
    private String locationID;
    private int quantity;
    private static SubscriptionManager<InventoryOuterClass.InventoryPulled> subscriptionManger = new SubscriptionManager<>(InventoryOuterClass.InventoryPulled.getDescriptor().getFullName());

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

    public static void subscribe(StreamObserver<InventoryOuterClass.InventoryPulled> observer) {
        subscriptionManger.subscribe(observer, 0);
    }
    @Override
    public void publish() {
        InventoryOuterClass.InventoryPulled event = InventoryOuterClass.InventoryPulled.newBuilder()
                .setOrderNumber(orderNumber)
                .setItemNumber(getItemNumber())
                .setLocation(locationID)
                .setQuantityPulled(quantity)
                .build();
        String description = "inventory.InventoryPulled Quantity " + quantity + " @ location " + locationID + " for Order " + orderNumber;
        subscriptionManger.publish(event, description);
    }
}
