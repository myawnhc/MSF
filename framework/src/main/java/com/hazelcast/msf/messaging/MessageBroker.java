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

package com.hazelcast.msf.messaging;

import com.hazelcast.map.IMap;
import com.hazelcast.msf.controller.MSFController;
import com.hazelcast.msf.transactions.TxnMessage;

/* Probably will be removed; fell out of the design very early in favor of using
   gRPC for service-to-service communication.
 */
@Deprecated
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
