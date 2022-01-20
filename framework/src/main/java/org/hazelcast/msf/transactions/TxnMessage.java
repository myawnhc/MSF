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

package org.hazelcast.msf.transactions;

import com.hazelcast.map.IMap;
import org.hazelcast.msf.controller.MSFController;

import java.io.Serializable;

@Deprecated // along with MessageBroker in favor of gRPC plus maybe ReliableTopic
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
