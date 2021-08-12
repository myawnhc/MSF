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
import com.hazelcast.sql.SqlResult;
import com.hazelcast.sql.SqlService;
import com.hazelcast.sql.SqlStatement;

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
        HazelcastInstance client = getClientConnection();
        map = client.getMap(mapName);
        sql = client.getSql();
    }

    private HazelcastInstance getClientConnection() {
        HazelcastInstance client = HazelcastClient.newHazelcastClient();
        return client;
    }

    public void disconnect() {
        getClientConnection().shutdown();
    }

    public IMap<K, T> getMap() { return map; }

    public void insert(K key, T dobj) {
        map.set(key, dobj);
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

    // Not tested; find some good use cases to build on this.
    // Can we work cooperatively with subclass to return domain objects rather than SqlResult?
    // Can't be universal since query may be a count(*) or similar
    public SqlResult query(String query) {
        SqlStatement statement = new SqlStatement(query);
        return sql.execute(statement);
    }

    // More flexible way to get to the SQL service
    public SqlService getSql() { return sql; }
}
