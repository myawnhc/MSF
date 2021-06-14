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

package com.hazelcast.msf.eventstore;

import java.util.function.UnaryOperator;

// T is type of the domain object for the event being sequenced, used to materialize T from the event store
// concrete subclass must implement apply(T) for materialization
public abstract class SequencedEvent<T> implements UnaryOperator<T> {

    // Now that all sequencing is back in the EventStore rather than here,
    // we could could rename this, but Event is so overloaded I'd rather
    // not add to the confusion.

//    private long sequence;

//    public void setSequence(Long sequence) {
//        this.sequence =  sequence;
//    }
//    public long getSequence() { return sequence; }

}
