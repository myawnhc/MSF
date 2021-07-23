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

package com.hazelcast.msf.controller;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.YamlClientConfigBuilder;
import com.hazelcast.collection.IList;
import com.hazelcast.config.Config;
import com.hazelcast.config.IndexConfig;
import com.hazelcast.config.IndexType;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.YamlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.IAtomicLong;
import com.hazelcast.cp.ICountDownLatch;
import com.hazelcast.flakeidgen.FlakeIdGenerator;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.map.IMap;
import com.hazelcast.multimap.MultiMap;
import com.hazelcast.sql.SqlService;
import com.hazelcast.topic.ITopic;

import java.io.File;
import java.util.Collection;

/** Controller class to serve as central point-of-management for Hazelcast; will be responsible for
 *  configuration, starting and stopping the cluster, and providing an interface to Hazelcast
 *  APIs so that individual services don't need to hold IMDG or Jet references.
 */
public class MSFController {

    private HazelcastInstance hazelcast;
    private FlakeIdGenerator messageID;

    private MultiMap<String, String> servicesStarted;

    // Singleton implementation
    private MSFController() { this.init(); }
    private static class Singleton {
        private static final MSFController INSTANCE = new MSFController();
    }
    public static MSFController getInstance() {
        return Singleton.INSTANCE;
    }

    private void init() {

        // TODO: make configurable - maybe add to service.yaml.test?
        boolean embedded = true;

        if (embedded) {
            Config config = new YamlConfigBuilder().build();
            config.getJetConfig().setEnabled(true);
            System.out.println("MSFController starting Hazelcast Platform embedded instance with config from classpath");
            hazelcast = Hazelcast.newHazelcastInstance(config);
        } else {
            ClientConfig config = new YamlClientConfigBuilder().build();
            System.out.println("MSFController starting Hazelcast Platform client with config from classpath");
            hazelcast = HazelcastClient.newHazelcastClient(config);
        }
        messageID = hazelcast.getFlakeIdGenerator("messageID");
        servicesStarted = hazelcast.getMultiMap("servicesRunning");
    }

    // Probably an unnecessary level of indirection, but thought there might
    // be some configuration we'd do here around eviction, TTL, etc. that
    // would be managed by the framework and not the individual clients.
    public IMap getMap(String name) { return hazelcast.getMap(name); }
    public ITopic getTopic(String name) { return hazelcast.getReliableTopic(name); }
    public long getUniqueMessageID() { return messageID.newId(); }
    public IList getList(String name) { return hazelcast.getList(name); }
    public ICountDownLatch getCountDownLatch(String name) { return hazelcast.getCPSubsystem().getCountDownLatch(name); }

    // Might move this to EventStore base class
    public IMap createEventStore(String mapName, String keyFieldName) {
        Config config = new Config();
        MapConfig mapConfig = config.getMapConfig(keyFieldName);
        IndexConfig timeStampIndex = new IndexConfig(IndexType.SORTED);
        timeStampIndex.addAttribute("timestamp");
        mapConfig.addIndexConfig(timeStampIndex);
        IndexConfig keyIndex = new IndexConfig(IndexType.HASH);
        keyIndex.addAttribute(keyFieldName);
        mapConfig.addIndexConfig(timeStampIndex);
        mapConfig.addIndexConfig(keyIndex);
        config.addMapConfig(mapConfig);
        hazelcast.getConfig().addMapConfig(mapConfig);
        return hazelcast.getMap(mapName);
    }

    // Get unique ids for anything other than messages - initially Accounts
    public long getUniqueId(String category) {
        return hazelcast.getFlakeIdGenerator(category).newId();
    }

    // Atomic longs are used to provide sequence numbers for event stores
    public IAtomicLong getSequenceGenerator(String name) {
        return hazelcast.getCPSubsystem().getAtomicLong(name);
    }

    // Not currently used, but if we want to shut down cleanly we have to have some
    // way of knowing when all services have stopped running.
    public void startService(String service) {
        servicesStarted.put(service, null);  // might not be legal
    }
    public void stopService(String service) {
        Collection<String> jobsRunning = servicesStarted.get(service);
        for (String job : jobsRunning) {
            hazelcast.getJet().getJob(job).cancel();
        }
        servicesStarted.remove(service);
        // TODO: use logger
        System.out.println("All jobs for " + service + " have initiated shutdown.");
    }

    // By moving job control out of the service, we can let the framework decide
    // (likely via configuration) whether we're doing embedded or client/server,
    // how many instances to start, etc.
    public void startJob(String service, String jobName, File serviceJar, Pipeline p) {
        JobConfig jconfig = new JobConfig();
        jconfig.setName(jobName);

        // Always add the Framework jar
        File f = new File("./framework/target/framework-1.0-SNAPSHOT.jar");
        //System.out.println("Found framework: " + f.exists());
        //jconfig.addJar(f);

        // Service will submit its own jar
        //jconfig.addJar(serviceJar);

        System.out.println("MSFController starting job " + jobName);
        try {
            hazelcast.getJet().newJobIfAbsent(p, jconfig);
            servicesStarted.put(service, jobName);
            System.out.println(servicesStarted.get(service).size() + " jobs now running for " + service);
        } catch (Throwable t) {
            System.out.println("Job start failed " + t.getMessage());
        }
    }

    public SqlService getSqlService() { return hazelcast.getSql(); }

}
