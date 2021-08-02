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

import com.hazelcast.topic.Message;
import com.hazelcast.topic.ReliableMessageListener;

@Deprecated // May find another use for this but event publishing is not using it
public class EventMsgListener<T> implements ReliableMessageListener<T> {
    private long sequence = 0;

    @Override
    public long retrieveInitialSequence() {
        return sequence;
    }

    @Override
    public void storeSequence(long l) {
        this.sequence = l;
    }

    @Override
    public boolean isLossTolerant() {
        return false;
    }

    @Override
    public boolean isTerminal(Throwable throwable) {
        return false;
    }

    // May remove and make class abstract
    @Override
    public void onMessage(Message<T> message) {
        System.out.println("Override me!");
    }
}
