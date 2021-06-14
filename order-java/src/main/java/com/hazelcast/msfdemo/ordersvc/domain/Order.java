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

package com.hazelcast.msfdemo.ordersvc.domain;

import com.hazelcast.msf.persistence.DTO;

import java.io.Serializable;
import java.util.EnumSet;

public class Order extends DTO<String> implements Serializable {

    private String orderNumber;
    private String acctNumber;
    private String itemNumber;
    private String location;
    private int extendedPrice;
    private int quantity;
    private EnumSet<WaitingOn> waitingOn;

    public Order() {}

    // Commands
    public void setOrderNumber(String orderNum) { this.orderNumber = orderNum; }
    public String getOrderNumber() { return this.orderNumber; }

    public void setAcctNumber(String acctNum) { this.acctNumber = acctNum; }
    public String getAcctNumber() { return this.acctNumber; }

    public void setItemNumber(String itemNum) { this.itemNumber = itemNum; }
    public String getItemNumber() { return this.itemNumber; }

    public void setLocation(String location) { this.location = location; }
    public String getLocation() { return location; }

    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getQuantity() { return quantity; }

    public void setExtendedPrice(int price) { this.extendedPrice = price; }
    public int getExtendedPrice() { return extendedPrice; }

    public EnumSet<WaitingOn> getWaitingOn() {
        return waitingOn;
    }

    public void setWaitingOn(EnumSet<WaitingOn> waitingOn) {
        this.waitingOn = waitingOn;
    }
}
