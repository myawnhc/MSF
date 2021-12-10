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

import org.hazelcast.msfdemo.invsvc.domain.Item;

import java.io.Serializable;
import java.util.function.UnaryOperator;

public class CompactionEvent extends InventoryEvent implements Serializable, UnaryOperator<Item> {

    public CompactionEvent(String itemNumber, String description, String location,
                           String locationType, int qoh, int reserved, int atp) {
        super(InventoryEventTypes.COMPACTION);
        // TODO: Set all fields
    }

    @Override
    public void publish() {
        System.out.println("****** CompactionEvent.publish unimplemented!");
    }

     @Override // UnaryOperator<Account>
    public Item apply(Item item) {
        // TODO: set all fields
//         item.setAcctNumber(super.getAccountNumber());
//         item.setName(getAccountName());
//         item.setBalance(super.getAmount());
        return item;
    }
}
