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

package org.hazelcast.msfdemo.acctsvc.eventstore;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.nio.serialization.compact.CompactReader;
import com.hazelcast.nio.serialization.compact.CompactSerializer;
import com.hazelcast.nio.serialization.compact.CompactWriter;

import javax.annotation.Nonnull;
import java.io.IOException;

/* Experimental and not yet fully implemented -- so far just the write behavior */
public class AccountEventStoreSerializer implements CompactSerializer<AccountEventStore>, HazelcastInstanceAware {

    private HazelcastInstance hazelcast;

    @Nonnull
    @Override
    public AccountEventStore read(@Nonnull CompactReader compactReader) throws IOException {
        String mapName = compactReader.readString("mapName");
        String sequenceProviderName = compactReader.readString("sequenceProviderName");
        if (hazelcast == null) {
            throw new IllegalStateException("AESSerializer has no hazelcast instance");
        }
       // AccountEventStore aes = new AccountEventStore(); private constructor
        Object o = compactReader.readObject("domainObjectInit");
        // TODO: gotta set them all!  get map and SP from the instance
        return null; // should be aes;
    }

    @Override
    public void write(@Nonnull CompactWriter compactWriter, @Nonnull AccountEventStore accountEventStore) throws IOException {
        compactWriter.writeString("mapName", accountEventStore.getEventMap().getName());
        compactWriter.writeString("sequenceProviderName", accountEventStore.getSequenceProvider().getName());
        compactWriter.writeObject("domainObjectInit", accountEventStore.getDomainObjectConstructor());
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        System.out.println("AccountEventStoreSerializer has an instance now!");
        this.hazelcast = hazelcastInstance;
    }
}
