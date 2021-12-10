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

package org.hazelcast.msfdemo.acctsvc.views;

import com.hazelcast.map.IMap;
import com.hazelcast.map.listener.EntryAddedListener;
import org.hazelcast.msf.configuration.ServiceConfig;
import org.hazelcast.msf.controller.MSFController;
import org.hazelcast.msf.messaging.APIResponse;
import org.hazelcast.msfdemo.acctsvc.domain.Account;
import org.hazelcast.msfdemo.acctsvc.events.AccountEvent;
import org.hazelcast.msfdemo.acctsvc.events.AccountEventTypes;
import org.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.OpenAccountRequest;
import org.hazelcast.msfdemo.acctsvc.eventstore.AccountEventStore;
import org.hazelcast.msfdemo.acctsvc.service.AccountService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AccountDAOTest {

    AccountDAO dao;
    AccountEventStore eventStore;

    MSFController controller = MSFController.getInstance();
    String requestMapName = AccountEventTypes.OPEN.getQualifiedName();
    IMap<Long, OpenAccountRequest> pipelineInput = controller.getMap(requestMapName);
    String resultMapName = requestMapName + "Results";
    IMap<Long, APIResponse<String>> pipelineOutput = controller.getMap(resultMapName);

    String acctNumber; // used by map listener

    @BeforeEach
    void setUp() {
        //MSFController controller = MSFController.getInstance();
        ServiceConfig.ServiceProperties props = ServiceConfig.get("account-service");
        AccountService service = new AccountService();
        service.init(props.isEmbedded(), props.getClientConfig());
        dao = new AccountDAO();
        eventStore = service.getEventStore();
        eventStore.getEventMap().clear();
    }

    @AfterEach
    void tearDown() {
        // TODO: should have service shutdown method; if so we would not need
        // to do the map clear in setup.
    }

    @Test
    public void daoIsInitialized() {
        assertNotNull(dao);
    }

    @Test
    public void daoMatchesEventStore() throws InterruptedException {
        IMap<Long, AccountEvent> map = eventStore.getEventMap();
        assertEquals(0, map.size());

        OpenAccountRequest open = OpenAccountRequest.newBuilder().setAccountName("Test-001")
                .setInitialBalance(1000)
                .build();

        // Listen for response before triggering the pipeline
        // No need for predicate as we're the only writer
        UUID listenerID = pipelineOutput.addEntryListener((EntryAddedListener<Long, APIResponse<String>>) entryEvent -> {
            //System.out.println("OPEN completion listener fired for ID " + uniqueID);
            APIResponse<String> apiResponse = entryEvent.getValue();
            if (apiResponse.getStatus() == APIResponse.Status.SUCCESS) {
                acctNumber = apiResponse.getResultValue();
            }
        }, true);

        // Submit to pipeline
        long uniqueID = controller.getUniqueMessageID();
        pipelineInput.put(uniqueID, open);

        while (acctNumber == null) {
            System.out.println("waiting on pipeline");
            Thread.sleep(1000);
        }
        System.out.println("Got AccountNumber " + acctNumber);
        // TODO: Can either get response or depend on fact there will only be
        // one account to get it via .keySet[0]

        // TODO: Haven't written AdjustBalance proto and pipeline yet,
        // will do a more full-featured test when they are done.

        // Series matches AccountEventStoreTest.materializeAdjustments
        // so we know it produces good result in the materialize() method
//        adjust("001", 100);
//        adjust("001", 200);
//        adjust("001", 300);



        Account a = eventStore.materialize(acctNumber);
        assertNotNull(a);
        Account b = dao.findByKey(acctNumber);
        assertNotNull(b);
        assertEquals(a.getBalance(), b.getBalance());
        assertEquals(a.getName(), b.getName());
        assertEquals(a.getAcctNumber(), b.getAcctNumber());
    }

//    private void open(String acctNum, int balance) {
//        AccountEvent event = new OpenAccountEvent(acctNum, "Test-"+acctNum, balance);
//        eventStore.append(event);
//    }
//
//    private void adjust(String acctNum, int balance) {
//        AccountEvent event = new AdjustBalanceEvent(acctNum, balance);
//        eventStore.append(event);
//    }
}
