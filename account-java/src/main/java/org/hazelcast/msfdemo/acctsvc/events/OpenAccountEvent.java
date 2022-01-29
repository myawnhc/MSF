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

package org.hazelcast.msfdemo.acctsvc.events;

import com.hazelcast.core.HazelcastInstance;
import io.grpc.stub.StreamObserver;
import org.hazelcast.msf.eventstore.SubscriptionManager;
import org.hazelcast.msfdemo.acctsvc.domain.Account;
import org.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AccountOpened;

import java.io.Serializable;
import java.util.function.UnaryOperator;

public class OpenAccountEvent extends AccountEvent implements Serializable,
        UnaryOperator<Account> {

    private String accountName;

    private static SubscriptionManager<AccountOpened> subscriptionManager;

    // Called from pipeline that creates the events
    public synchronized static void setHazelcastInstance(HazelcastInstance hz) {
        if (subscriptionManager == null) {
            subscriptionManager = new SubscriptionManager<AccountOpened>(hz, AccountOpened.getDescriptor().getFullName());
            subscriptionManager.setVerbose(false);
        }
    }

    public OpenAccountEvent(String accountNumber, String accountName, int openingBalance) {
        super(AccountEventTypes.OPEN, accountNumber, openingBalance);
        this.accountName = accountName;
    }

    public String getAccountName() { return accountName; }
    public String toString() {
        return "OPEN account " + accountNumber + " with initial balance " + amount;
    }

    // Because this is static we can't make it part of SequencedEvent API
    public static void subscribe(StreamObserver<AccountOpened> observer) {
        subscriptionManager.subscribe(observer, 0);
    }

    @Override
    public void publish() {
        AccountOpened event = AccountOpened.newBuilder()
                .setAccountNumber(accountNumber)
                .setAccountName(accountName)
                .setInitalBalance(amount)
                .build();
        subscriptionManager.publish(event, toString());
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
