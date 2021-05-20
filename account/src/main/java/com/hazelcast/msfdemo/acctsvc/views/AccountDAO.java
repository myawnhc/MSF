package com.hazelcast.msfdemo.acctsvc.views;

import com.hazelcast.aggregation.Aggregators;
import com.hazelcast.msf.persistence.DAO;
import com.hazelcast.msfdemo.acctsvc.domain.Account;

public class AccountDAO extends DAO<Account, String> {

    public AccountDAO() {
        super("account");
    }

    // Non-inheritable query methods
    public long getTotalAccountBalances() {
        return getMap().aggregate(Aggregators.integerSum("balance"));
    }
}
