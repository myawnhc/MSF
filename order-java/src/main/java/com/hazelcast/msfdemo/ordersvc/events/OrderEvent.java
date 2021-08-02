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

import com.hazelcast.msf.eventstore.SequencedEvent;
import com.hazelcast.msfdemo.ordersvc.domain.Order;

import java.io.Serializable;

public abstract class OrderEvent extends SequencedEvent<Order> implements Serializable {

    //protected OrderEventTypes eventType;
    protected String orderNumber;
//    protected String accountNumber;
//    protected String itemNumber;
//    protected int    quantity;
//    protected String location;
//    protected int    extendedPrice;
//    protected boolean terminal;

//    public OrderEvent(OrderEventTypes type) {
//        super(null);
//        this.eventType = type;
//    }

    public OrderEvent(String orderNumber) {
        this.orderNumber = orderNumber;
    }

//    public OrderEvent(OrderEventTypes type, String orderNumber, String accountNumber,
//                      String itemNumber, String location, int quantity) {
//        super(null);
//        this.eventType = type;
//        this.orderNumber = orderNumber;
//        this.accountNumber = accountNumber;
//        this.itemNumber = itemNumber;
//        this.location = location;
//        this.quantity = quantity;
//    }

    public String getOrderNumber() { return orderNumber; }
//    public String getAccountNumber() { return accountNumber; }
//    public String getItemNumber() { return itemNumber; }
//    public String getLocation() { return location; }
//    public int getQuantity() { return quantity; }
//    public int getExtendedPrice() { return extendedPrice; }
//    public String getEventName() { return eventType.getQualifiedName(); }
//    public boolean isTerminal() { return terminal; }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

//    public void setAccountNumber(String accountNumber) {
//        this.accountNumber = accountNumber;
//    }
//
//    public void setItemNumber(String itemNumber) {
//        this.itemNumber = itemNumber;
//    }
//
//    public void setQuantity(int quantity) {
//        this.quantity = quantity;
//    }
//
//    public void setLocation(String location) {
//        this.location = location;
//    }
//
//    public void setExtendedPrice(int extendedPrice) {
//        this.extendedPrice = extendedPrice;
//    }

//    @Override
//    public String toString() {
//        return eventType.getQualifiedName() + " O:" + orderNumber + " A:" + accountNumber +
//                " I: " + itemNumber + " L: " + location +
//                " Q: " + quantity + " $: " + extendedPrice + " TERM?: " + isTerminal();
//    }
}
