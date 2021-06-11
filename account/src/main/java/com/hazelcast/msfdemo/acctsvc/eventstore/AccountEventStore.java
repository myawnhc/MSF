package com.hazelcast.msfdemo.acctsvc.eventstore;

import com.hazelcast.msf.controller.MSFController;
import com.hazelcast.msf.eventstore.EventStore;
import com.hazelcast.msfdemo.acctsvc.domain.Account;
import com.hazelcast.msfdemo.acctsvc.events.AccountEvent;
import com.hazelcast.msfdemo.acctsvc.events.CompactionEvent;

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

    // Materialize method generified and moved to EventStore base class

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

    public CompactionEvent writeAsCheckpoint(Account account, long sequence) {
        CompactionEvent checkpoint = new CompactionEvent(account.getAcctNumber(), account.getName(), account.getBalance());
        return checkpoint;
    }
}
