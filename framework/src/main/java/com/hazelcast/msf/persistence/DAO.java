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

package com.hazelcast.msf.persistence;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.sql.SqlService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/* T = domain object type
   K = key type
 */
public abstract class DAO<T extends DTO<K>, K> {
    protected IMap<K, T> map;
    protected SqlService sql;

    public DAO(String mapName) {
        //MSFController controller = MSFController.getInstance();
        HazelcastInstance client = getClientConnection();
        map = client.getMap(mapName);
        sql = client.getSql();
    }

    private HazelcastInstance getClientConnection() {
        //String configname = "embedded";
        //String configname = ConfigUtil.findConfigNameInArgs(args);
        // Jet doesn't accept a clientConfig argument, only Config().
        //ClientConfig clientConfig = ConfigUtil.getClientConfigForCluster(configname);
        // TODO: this shouldn't be a hard-coded address!
        //clientConfig.getNetworkConfig().addAddress("172.17.0.2:5701");
        //clientConfig.setClusterName("dev"); // dev when embedded, jet when client-server
        HazelcastInstance client = HazelcastClient.newHazelcastClient();
        return client;
    }

    public void disconnect() {
        getClientConnection().shutdown();
    }

    public IMap<K, T> getMap() { return map; }

    public void insert(K key, T dobj) {
        // Do we need a separate Map (O/R mapping) object?  If so it should
        // be reachable from the DO so shouldn't need additional parameter
        map.set(key, dobj); // put here will trigger a db load for prev value that we don't need
    };
    public void delete(T dobj) {
        map.remove(dobj.getKey());
    }
    public void deleteByKey(K key) {
        map.remove(key);
    }
    public void update(T dobj) {
        map.put(dobj.getKey(), dobj);
    }
    public T findByKey(K key) {
        return map.get(key);
    }
    public List<T> findAll() {
        Collection<T> values = map.values();
        return Collections.unmodifiableList(new ArrayList<>(values));
    }
}
