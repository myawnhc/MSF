package com.hazelcast.msfdemo.acctsvc.events;

import com.hazelcast.msfdemo.acctsvc.domain.Account;

import java.io.Serializable;
import java.util.function.UnaryOperator;

public class AdjustBalanceEvent extends AccountEvent implements Serializable,
                                                                UnaryOperator<Account> {

    public AdjustBalanceEvent(String accountNumber, int adjustment) {
        super(AccountEventTypes.ADJUST, accountNumber, adjustment);
    }
    @Override
    public Account apply(Account account) {
        account.setBalance( account.getBalance() + getAmount() );
        //System.out.println("AdjustBalanceEvent.apply - " + account.getAcctNumber() + " with " + getAmount());
        return account;
    }

    public String toString() {
        return "ADJUST account " + accountNumber + " change by: " + amount;
    }
}
