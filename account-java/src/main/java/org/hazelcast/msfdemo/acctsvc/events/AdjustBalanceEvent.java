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
import org.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.BalanceChanged;

import java.io.Serializable;
import java.util.function.UnaryOperator;

public class AdjustBalanceEvent extends AccountEvent implements Serializable,
                                                                UnaryOperator<Account> {

    private int changeAmount;

    private static SubscriptionManager<BalanceChanged> subscriptionManager;

    public synchronized static void setHazelcastInstance(HazelcastInstance hz) {
        //hazelcast = hz;
        if (subscriptionManager == null) {
            subscriptionManager = new SubscriptionManager<BalanceChanged>(hz, BalanceChanged.getDescriptor().getFullName());
            subscriptionManager.setVerbose(false);
        }
    }

    public AdjustBalanceEvent(String accountNumber, int adjustment) {
        super(AccountEventTypes.ADJUST, accountNumber, adjustment);
    }

    @Override
    public Account apply(Account account) {
        this.changeAmount = getAmount();
        account.setBalance( account.getBalance() + getAmount() );
        //System.out.println("AdjustBalanceEvent.apply - " + account.getAcctNumber() + " with " + getAmount());
        return account;
    }

    public static void subscribe(StreamObserver<BalanceChanged> observer) {
        subscriptionManager.subscribe(observer, 0);
    }

    @Override
    public void publish() {
        BalanceChanged event = BalanceChanged.newBuilder()
                .setAccountNumber(accountNumber)
                .setChangeAmount(changeAmount)
                .setNewBalance(getAmount())
                .build();
        subscriptionManager.publish(event);
    }

    public String toString() {
        return "ADJUST account " + accountNumber + " change by: " + amount;
    }
}
