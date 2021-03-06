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

package org.hazelcast.msfdemo.acctsvc.views;

import com.hazelcast.aggregation.Aggregators;
import org.hazelcast.msf.controller.MSFController;
import org.hazelcast.msf.persistence.DAO;
import org.hazelcast.msfdemo.acctsvc.domain.Account;

import java.util.Collection;

public class AccountDAO extends DAO<Account, String> {

    public AccountDAO(MSFController controller) {
        super(controller, "account");
    }

    // Non-inheritable query methods

    public Collection<Account> getAllAccounts() {
        return getMap().values();
    }
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
