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

package org.hazelcast.msfdemo.acctsvc.events;

import org.hazelcast.msfdemo.acctsvc.domain.Account;

import java.io.Serializable;
import java.util.function.UnaryOperator;

public class CompactionEvent extends AccountEvent implements Serializable,
        UnaryOperator<Account> {

    private String accountName;

    public CompactionEvent(String accountNumber, String accountName, int amount) {
        super(AccountEventTypes.COMPACTION, accountNumber, amount);
        this.accountName = accountName;
    }

    public String getAccountName() { return accountName; }
    public String toString() {
        return "COMPACT account " + accountNumber + " with carry-forward balance " + amount;
    }

    @Override
    public void publish() {
        System.out.println("****** CompactionEvent.publish unimplemented!");
    }

    @Override // UnaryOperator<Account>
    public Account apply(Account account) {
        account.setAcctNumber(super.getAccountNumber());
        account.setName(getAccountName());
        account.setBalance(super.getAmount());
        //System.out.println("OpenAccountEvent.apply - " + account.getAcctNumber() + " with " + getAmount());
        return account;
    }
}
