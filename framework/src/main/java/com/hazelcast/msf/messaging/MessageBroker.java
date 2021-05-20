package com.hazelcast.msf.messaging;

import com.hazelcast.map.IMap;
import com.hazelcast.msf.controller.MSFController;
import com.hazelcast.msf.transactions.TxnMessage;

public class MessageBroker {

    static private MSFController controller = MSFController.getInstance();
    // TODO: should use a FlakeID or something here ...


    public static void publish(String topic, TxnMessage message) {
        IMap<Long, TxnMessage> messageMap = controller.getMap(topic);
        messageMap.put(controller.getUniqueMessageID(), message);
    }

//    public static void subscribe(String topic, MessageListener callback) {
//        ITopic rTopic = controller.getTopic(topic);
//        rTopic.addMessageListener(callback);
//    }




}
