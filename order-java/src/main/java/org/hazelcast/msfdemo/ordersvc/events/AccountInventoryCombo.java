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

import java.io.Serializable;

public class AccountInventoryCombo implements Serializable {
    private String orderNumber;
    // Account fields
    private String accountNumber;
    private int amountCharged;
    // Inventory fields;
    private String itemNumber;
    private String location;
    private int quantity;

    public void setAccountFields(CreditCheckEvent in) {
        this.orderNumber = in.getOrderNumber();
        this.accountNumber = in.getAccountNumber();
        this.amountCharged = in.getAmountRequested();
    }

    public void setAccountFields(ChargeAccountEvent in) {
        this.orderNumber = in.getOrderNumber();
        this.accountNumber = in.getAccountNumber();
        this.amountCharged = in.getAmountRequested();
    }
    public void setInventoryFields(InventoryReserveEvent in) {
        this.orderNumber = in.getOrderNumber();
        this.itemNumber = in.getItemNumber();
        this.location = in.getLocation();
        this.quantity = in.getQuantity();
    }

    public void setInventoryFields(PullInventoryEvent in) {
        this.orderNumber = in.getOrderNumber();
        this.itemNumber = in.getItemNumber();
        this.location = in.getLocation();
        this.quantity = in.getQuantityPulled();
    }

    public boolean hasInventoryFields() {
        return itemNumber != null && location != null && quantity != 0;
    }

    public boolean hasAccountFields() {
        return accountNumber != null && amountCharged != 0;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public int getAmountCharged() {
        return amountCharged;
    }

    public String getItemNumber() {
        return itemNumber;
    }

    public String getLocation() {
        return location;
    }

    public int getQuantity() {
        return quantity;
    }
}
