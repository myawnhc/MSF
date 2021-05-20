package com.hazelcast.msfdemo.acctsvc.eventstore;

import com.hazelcast.map.IMap;
import com.hazelcast.msfdemo.acctsvc.domain.Account;
import com.hazelcast.msfdemo.acctsvc.events.AccountEvent;
import com.hazelcast.msfdemo.acctsvc.events.AdjustBalanceEvent;
import com.hazelcast.msfdemo.acctsvc.events.OpenAccountEvent;
import com.hazelcast.msfdemo.acctsvc.service.AccountService;
import com.hazelcast.query.Predicates;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class AccountEventStoreTest {

    AccountEventStore eventStore;

    @BeforeEach
    void setUp() {
        //MSFController controller = MSFController.getInstance();
        AccountService service = new AccountService();
        service.init();
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
        Account acct = eventStore.materialize("001");
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
        Account a = eventStore.materialize("001");
        assertEquals(1000+100+200+300, a.getBalance());
        assertEquals(4, eventStore.getEventMap().size());

        adjust("001", -250);
        a = eventStore.materialize("001");
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
            Account a = eventStore.materialize(acctNum);
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