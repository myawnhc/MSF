package com.hazelcast.msfdemo.acctsvc.views;

import com.hazelcast.map.IMap;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.msf.controller.MSFController;
import com.hazelcast.msf.messaging.APIResponse;
import com.hazelcast.msfdemo.acctsvc.domain.Account;
import com.hazelcast.msfdemo.acctsvc.events.AccountEvent;
import com.hazelcast.msfdemo.acctsvc.events.AccountEventTypes;
import com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass;
import com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.OpenAccountRequest;
import com.hazelcast.msfdemo.acctsvc.events.AdjustBalanceEvent;
import com.hazelcast.msfdemo.acctsvc.events.OpenAccountEvent;
import com.hazelcast.msfdemo.acctsvc.eventstore.AccountEventStore;
import com.hazelcast.msfdemo.acctsvc.service.AccountService;
import com.hazelcast.query.Predicates;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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
        AccountService service = new AccountService();
        service.init();
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
