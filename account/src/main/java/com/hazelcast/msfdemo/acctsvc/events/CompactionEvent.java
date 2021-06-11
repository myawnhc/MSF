package com.hazelcast.msfdemo.acctsvc.events;

import com.hazelcast.msfdemo.acctsvc.domain.Account;

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

    @Override // UnaryOperator<Account>
    public Account apply(Account account) {
        account.setAcctNumber(super.getAccountNumber());
        account.setName(getAccountName());
        account.setBalance(super.getAmount());
        //System.out.println("OpenAccountEvent.apply - " + account.getAcctNumber() + " with " + getAmount());
        return account;
    }
}
