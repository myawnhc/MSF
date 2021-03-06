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

package org.hazelcast.msf.eventstore;

import java.util.function.UnaryOperator;

// T is type of the domain object for the event being sequenced, used to materialize T from the event store
// concrete subclass must implement apply(T) for materialization
public abstract class SequencedEvent<T> implements UnaryOperator<T> {

    public String getEventName() {
        return this.getClass().getSimpleName();
    }
    // The published event is not of type T but an associated gRPC type
    abstract public void publish();
    // subscribe method is static on subclasses so can't define it here

}
