package com.hazelcast.msfdemo.acctsvc.events;

import com.hazelcast.msf.eventstore.SequencedEvent;
import com.hazelcast.msfdemo.acctsvc.domain.Account;

import java.io.Serializable;
import java.util.function.UnaryOperator;

public abstract class AccountEvent extends SequencedEvent
        implements Serializable, UnaryOperator<Account> {
    private AccountEventTypes eventType;
    protected String accountNumber;
    protected int amount;

    public AccountEvent(AccountEventTypes type, String accountNumber, int amount) {
        this.eventType = type;
        this.accountNumber = accountNumber;
        this.amount = amount;
    }

    public String getAccountNumber() { return accountNumber; }
    public int getAmount() { return amount; }
}
