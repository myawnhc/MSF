package com.hazelcast.msf.persistence;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.msf.controller.MSFController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/* T = domain object type
   K = key type
 */
public abstract class DAO<T extends DTO<K>, K> {
    //HazelcastInstance hazelcast;
    IMap<K, T> map;

    public DAO(String mapName) {
        //hazelcast = Hazelcast.newHazelcastInstance();
        MSFController controller = MSFController.getInstance();
        map = controller.getMap(mapName);
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
