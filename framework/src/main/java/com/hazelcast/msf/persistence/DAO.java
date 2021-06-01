package com.hazelcast.msf.persistence;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.msf.configuration.ConfigUtil;
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
        String configname = "embedded";
        //String configname = ConfigUtil.findConfigNameInArgs(args);
        // Jet doesn't accept a clientConfig argument, only Config().
        ClientConfig clientConfig = ConfigUtil.getClientConfigForCluster(configname);
        // TODO: this shouldn't be a hard-coded address!
        //clientConfig.getNetworkConfig().addAddress("172.17.0.2:5701");
        clientConfig.setClusterName("dev"); // dev when embedded, jet when client-server
        HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);
        return client;
    }

    public void disconnect() {
        getClientConnection().shutdown();
    }

    public IMap<K, T> getMap() { return map; }

    public void insert(K key, T dobj) {
        // Do we need a separate Map (O/R mapping) object?  If so it should
        // be reachable from the DO so shouldn't need additional parameter
        map.put(key, dobj);
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
