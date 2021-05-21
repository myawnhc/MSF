package com.hazelcast.msfdemo.acctsvc.eventstore;

import com.hazelcast.msf.controller.MSFController;
import com.hazelcast.msf.eventstore.EventStore;
import com.hazelcast.msfdemo.acctsvc.domain.Account;
import com.hazelcast.msfdemo.acctsvc.events.AccountEvent;
import com.hazelcast.query.Predicates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AccountEventStore extends EventStore<Account, String, AccountEvent> {

    // Singleton implementation
    private AccountEventStore() {
        super(AccountEventStore.class.getCanonicalName(), Account::new);
        MSFController controller = MSFController.getInstance();
        String mapName = "AccountEventStore";
        String keyName = "accountNumber"; // builds an index, so case sensitive!
        eventMap = controller.createEventStore(mapName, keyName);
    }
    private static class Singleton {
        private static final AccountEventStore INSTANCE = new AccountEventStore();
    }
    public static AccountEventStore getInstance() {
        return Singleton.INSTANCE;
    }

//    // Build a materialized view from the Event Store.  Should not be necessary
//    // in normal operation as we do this on-the-fly, but if we are in recovery
//    // scenario or taking a snapshot, then we do this.
//    public Account materialize(String acctNum) {
//        Account account = new Account();
//        List<Long> keys = new ArrayList(eventMap.keySet(Predicates.sql("accountNumber="+acctNum)));
//        Collections.sort(keys);
//        for (Long sequence : keys) {
//            AccountEvent accountEvent = eventMap.get(sequence);
//            accountEvent.apply(account);
//        }
//        return account;
//    }

    // Is this an all-or-nothing operation?  Maybe we want to use it for space
    // management so might set a threshold - checkpoint keys having over X entries.
    // Could also checkpoint entries older than X, but in that case we're
    // no longer append-only -- but could work around that by making a copy as
    // we go.
    public void snapshot() {
        // Get KeySet from map
        // For each key:
        //    Account account = materialize(key)
        //    Remove all entries for the key from the event store
        //    Append the snapshot record to the event store
        //    (maybe flip order of those last two so we have less risk of data loss)
    }
}
