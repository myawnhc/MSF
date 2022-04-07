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

package org.hazelcast.msf.messaging;

import com.hazelcast.cp.IAtomicLong;
import com.hazelcast.map.IMap;
import io.grpc.stub.StreamObserver;

// V is a GRPC message type - GeneratedMessageV3 - if that helps to know at some point
public class StreamObserverToIMapAdapter<V> implements StreamObserver<V> {
    final private IMap<Long, V> map;
    final private IAtomicLong sequence;

    public StreamObserverToIMapAdapter(IMap<Long, V> map, IAtomicLong sequence) {
        this.map = map;
        this.sequence = sequence;
    }
    @Override
    public void onNext(V v) {
        long key = sequence.getAndIncrement();
        map.set(key, v);
        //System.out.println("StreamObserverToIMapAdapter writes " + v.getClass().getSimpleName() + " to " + map.getName() + " sequence " + key);
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void onCompleted() {
        // nop
    }
}
