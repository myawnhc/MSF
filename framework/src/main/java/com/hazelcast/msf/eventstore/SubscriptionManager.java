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

import com.hazelcast.msf.controller.MSFController;
import com.hazelcast.ringbuffer.Ringbuffer;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;

// Mixin class; don't want this in SequencedEvent because it is per Event Class, not per
// event; but don't want to have to redefine it in every subclass
// There may be more parameterization coming ...
public class SubscriptionManager<T> {

    private String name;
    private List<StreamObserver<T>> subscribers;
    private Ringbuffer<T> ringBuffer;
    private boolean verbose = true;

    public SubscriptionManager(String eventName) {
        name = eventName;
        ringBuffer = MSFController.getInstance().getRingbuffer(name);
        subscribers = new ArrayList<>();
        if (verbose)
            System.out.println("Created subscription manager for " + name);
    }

    public void setVerbose(boolean setting) {
        verbose = setting;
    }

    public void subscribe(StreamObserver so, int offset) {
        subscribers.add(so);
        if (verbose)
            System.out.println("Added subscriber to " + name);
        if (offset != -1) {
            System.out.println(" Subscribed from offset " + offset + " with headSequence " + ringBuffer.headSequence());
            for (int index=offset; index<ringBuffer.headSequence(); index++) {
                // TODO: send this observer any missed events -
                //  everything from requested offset to current
                T oldMessage = null;
                try {
                    oldMessage = ringBuffer.readOne(index++);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (verbose)
                    System.out.println("Sending pre-recorded message");
                so.onNext(oldMessage);
            }
        }
    }

    // gRPC toString methods are one line per field so too noisy to log directly, so
    // adding an overload to publish that lets us specify what to log
    public void publish (T event, String description) {
        if (verbose) {
            if (description == null)
                description = name;
            System.out.println("SubscriptionManager publishing " + description + " to " + subscribers.size() + " subscribers");
        }
        ringBuffer.add(event);
        for (StreamObserver so : subscribers) {
            so.onNext(event);
        }
    }

    // May override to invoke subclass-specific gRPC builder for event objects
    public void publish(T event) {
        publish (event, null);
    }
}
