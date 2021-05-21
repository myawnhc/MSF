package com.hazelcast.msfdemo.acctsvc.events;

import com.hazelcast.msfdemo.acctsvc.domain.Account;

import java.io.Serializable;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class OpenAccountEvent extends AccountEvent implements Serializable,
        UnaryOperator<Account> {

    private String accountName;

    public OpenAccountEvent(String accountNumber, String accountName, int openingBalance) {
        super(AccountEventTypes.OPEN, accountNumber, openingBalance);
        this.accountName = accountName;
    }

    public String getAccountName() { return accountName; }
    public String toString() {
        return "OPEN account " + accountNumber + " with initial balance " + amount;
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
