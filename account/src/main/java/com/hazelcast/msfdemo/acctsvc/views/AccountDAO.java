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

        // Not yet - sum isn't in current SQL preview implementation
//        SqlResult result = sql.execute(new SqlStatement("select sum(balance) from account"));
//        Integer value = -1;
//        for (SqlRow row : result) {
//            value = row.getObject(0);
//            System.out.println("SQLRow value " + value);
//        }
//        return value.longValue();
    }
}
