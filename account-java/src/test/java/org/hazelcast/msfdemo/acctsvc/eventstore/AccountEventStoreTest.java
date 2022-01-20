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

package org.hazelcast.msfdemo.acctsvc.eventstore;

import com.hazelcast.map.IMap;
import com.hazelcast.query.Predicates;
import org.hazelcast.msf.configuration.ServiceConfig;
import org.hazelcast.msfdemo.acctsvc.domain.Account;
import org.hazelcast.msfdemo.acctsvc.events.AccountEvent;
import org.hazelcast.msfdemo.acctsvc.events.AdjustBalanceEvent;
import org.hazelcast.msfdemo.acctsvc.events.OpenAccountEvent;
import org.hazelcast.msfdemo.acctsvc.service.AccountService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AccountEventStoreTest {

    AccountEventStore eventStore;

    @BeforeEach
    void setUp() {
        //MSFController controller = MSFController.getInstance();
        ServiceConfig.ServiceProperties props = ServiceConfig.get("account-service");
        AccountService service = new AccountService();
        service.init(props.isEmbedded(), props.getClientConfig());
        eventStore = service.getEventStore();
        eventStore.getEventMap().clear();
    }

    @AfterEach
    void tearDown() {
        // TODO: should have service shutdown method; if so we would not need
        // to do the map clear in setup.
    }

    @Test
    // Insanely stupid test just to verify framework is working as expected
    void append() {
        IMap<Long, AccountEvent> map = eventStore.getEventMap();
        assertEquals(0, map.size());
        AccountEvent event = new OpenAccountEvent("001", "Test", 100);
        eventStore.append(event);
        assertEquals(1, map.size());
    }

    @Test
    void materializeOpen() {
        IMap<Long, AccountEvent> map = eventStore.getEventMap();
        assertEquals(0, map.size());

        // 01: Test account with OPEN only
        open("001", 100);
        Account acct = eventStore.materialize("accountNumber=001");
        assertNotNull(acct);
        assertEquals("001", acct.getAcctNumber());
        assertEquals("Test-001", acct.getName());
        assertEquals(100, acct.getBalance());
    }

    @Test
    void materializeAdjustments() {
        IMap<Long, AccountEvent> map = eventStore.getEventMap();
        assertEquals(0, map.size());

        // 01: Test account with OPEN only
        open("001", 1000);
        adjust("001", 100);
        adjust("001", 200);
        adjust("001", 300);
        Account a = eventStore.materialize("accountNumber=001");
        assertEquals(1000+100+200+300, a.getBalance());
        assertEquals(4, eventStore.getEventMap().size());

        adjust("001", -250);
        a = eventStore.materialize("accountNumber=001");
        assertEquals(1000+100+200+300-250, a.getBalance());
        assertEquals(5, eventStore.getEventMap().size());

    }

    @Test
    void materializeMany() {
        int howMany = 100;
        for (int i = 0; i<howMany; i++) {
            String acctNum = ""+ (i+1);
            open(acctNum, 1000);
            for (int j=1; j<4; j++) {
                adjust(acctNum, 100 * j);
            }
            for (int k=1; k<6; k++) {
                adjust(acctNum, -10*k);
            }
        }
        // 9 entries per account
        assertEquals(howMany*9, eventStore.getEventMap().size());
        for (int i=0; i<howMany; i++) {
            String acctNum = ""+ (i+1);
            System.out.println("Validating " + acctNum);
            Account a = eventStore.materialize("accountNumber=" + acctNum);
            assertEquals(1450, a.getBalance());
            Collection<AccountEvent> entries = eventStore.getEventMap().values(Predicates.sql("accountNumber=" + acctNum));
            assertEquals(9, entries.size());
            // TODO: assert ordereed by timestamp
        }
    }

    @Test
    void snapshot() {
    }

    private void open(String acctNum, int balance) {
        AccountEvent event = new OpenAccountEvent(acctNum, "Test-"+acctNum, balance);
        eventStore.append(event);
    }

    private void adjust(String acctNum, int balance) {
        AccountEvent event = new AdjustBalanceEvent(acctNum, balance);
        eventStore.append(event);
    }
}