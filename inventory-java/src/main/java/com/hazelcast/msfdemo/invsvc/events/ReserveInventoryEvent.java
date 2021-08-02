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

package com.hazelcast.msfdemo.invsvc.events;

import com.hazelcast.msf.eventstore.SubscriptionManager;
import com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryReserved;
import io.grpc.stub.StreamObserver;

import java.io.Serializable;

public class ReserveInventoryEvent extends InventoryEvent implements Serializable {
    private String orderNumber = "[not provided]";
    private String locationID;
    private int quantity;
    private static SubscriptionManager<InventoryReserved> subscriptionManger = new SubscriptionManager<>(InventoryReserved.getDescriptor().getFullName());

    public ReserveInventoryEvent() {
        super(InventoryEventTypes.RESERVE);
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

    public static void subscribe(StreamObserver<InventoryReserved> observer) {
        subscriptionManger.subscribe(observer, 0);
    }
    @Override
    public void publish() {
        InventoryReserved event = InventoryReserved.newBuilder()
                .setOrderNumber(orderNumber)
                .setLocation(locationID)
                .setQuantityReserved(quantity)
                .build();
        String description = "inventory.InventoryReserved Quantity " + quantity + " @ location " + locationID + " for Order " + orderNumber;
        subscriptionManger.publish(event, description);
    }
}
