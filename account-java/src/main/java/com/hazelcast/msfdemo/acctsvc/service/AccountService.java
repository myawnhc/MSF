/*
 * Copyright 2018-2021 Hazelcast, Inc
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
 *  limitations under the License.package com.theyawns.controller.launcher;
 *
 */

package com.hazelcast.msfdemo.acctsvc.service;

import com.hazelcast.map.IMap;
import com.hazelcast.msf.controller.MSFController;
import com.hazelcast.msfdemo.acctsvc.business.AdjustBalancePipeline;
import com.hazelcast.msfdemo.acctsvc.business.OpenAccountPipeline;
import com.hazelcast.msfdemo.acctsvc.domain.Account;
import com.hazelcast.msfdemo.acctsvc.eventstore.AccountEventStore;
import com.hazelcast.msfdemo.acctsvc.views.AccountDAO;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AccountService {

    MSFController controller;
    AccountDAO accountDAO;
    AccountEventStore eventStore;

    public void init() {
        controller = MSFController.getInstance();
        // DAO not currently being used but will be back as a Materialized View ...
        accountDAO = new AccountDAO();

        // Initialize the EventStore
        eventStore = AccountEventStore.getInstance();
        // TODO: should have process that occasionally snapshots & evicts

        // Start the various Jet transaction handler pipelines
        ExecutorService executor = Executors.newCachedThreadPool();
        OpenAccountPipeline openPipeline = new OpenAccountPipeline(this);
        executor.submit(openPipeline);

        AdjustBalancePipeline adjPipeline = new AdjustBalancePipeline(this);
        executor.submit(adjPipeline);

    }

    public AccountEventStore getEventStore() { return eventStore; }
    public IMap<String,Account> getView() { return accountDAO.getMap(); }

    public void shutdown() {
        // notify Hazelcast controller, it can shut down if no other
        // services are still running.
    }


//    // Obsolete: An example of doing transfer without the framework
//    public void transfer(String fromAccount, String toAccount, int amount) {
//        Account from = accountDAO.findByKey(fromAccount);
//        Account to = accountDAO.findByKey(toAccount);
//        from.debit(amount);
//        to.credit(amount);
//        accountDAO.update(from);
//        accountDAO.update(to);
//    }

    public static void main(String[] args) throws IOException, InterruptedException {
        AccountService acctService = new AccountService();
        acctService.init();

        final GrpcServer server = new GrpcServer();
        server.start();
        server.blockUntilShutdown();
    }
}
