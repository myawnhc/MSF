package com.hazelcast.msfdemo.acctsvc.domain;

import com.hazelcast.msf.persistence.DTO;

import java.io.Serializable;

public class Account extends DTO<String> implements Serializable {

    private int balance;    // in cents
    private String acctNumber;
    private String name;

    public Account() {}
    // Type: Credit Card, Checking -- these may be subclasses instead
    // Actions: Debit, Credit or charge, payment, withdrawal

    // Commands
//    public static Account open(String name, int beginningBalance) {
//        Account a = new Account();
//        a.name = name;
//        a.acctNumber = ""+ ++nextAcctNum;
//        a.balance = beginningBalance;
//        return a;
//    }

    public int getBalance() { return balance; }
    public void setBalance(int value) { balance = value; }

    public void setName(String name) { this.name = name; }
    public String getName() { return name; }

    public void setAcctNumber(String acctNum) { this.acctNumber = acctNum; }
    public String getAcctNumber() { return this.acctNumber; }

    public void credit(int byAmount) { balance += byAmount; }
    public void debit(int byAmount) { balance -= byAmount; }

}
