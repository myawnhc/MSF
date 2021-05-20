package com.hazelcast.msf.transactions;

import com.hazelcast.map.IMap;
import com.hazelcast.msf.controller.MSFController;

import java.io.Serializable;

public class TxnMessage implements Serializable {
    // Track these so we can clear them from the map when fully processed
    private long messageID;
    private String messageMap;

    // For use by serializers only
    protected TxnMessage() {}

    public TxnMessage(String map, long id) {
        this.messageMap = map;
        this.messageID = id;
    }

    public long getMessageID() { return messageID; }

    public void cleanup() {
        IMap<Long, TxnMessage> map = MSFController.getInstance().getMap(messageMap);
        map.remove(messageID);
        System.out.println("Cleanup removed " + messageID + " from " + messageMap);
    }
}
