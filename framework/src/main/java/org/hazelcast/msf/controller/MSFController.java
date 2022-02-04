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

package org.hazelcast.msf.controller;

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
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.cp.IAtomicLong;
import com.hazelcast.cp.ICountDownLatch;
import com.hazelcast.flakeidgen.FlakeIdGenerator;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.map.IMap;
import com.hazelcast.multimap.MultiMap;
import com.hazelcast.ringbuffer.Ringbuffer;
import com.hazelcast.sql.SqlService;
import com.hazelcast.topic.ITopic;
import org.hazelcast.msf.eventstore.SequencedEvent;
import org.hazelcast.msf.messaging.EventMsgListener;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/** Controller class to serve as central point-of-management for Hazelcast; will be responsible for
 *  configuration, starting and stopping the cluster, and providing an interface to Hazelcast
 *  APIs so that individual services don't need to hold IMDG or Jet references.
 */
public class MSFController {

    private HazelcastInstance hazelcast;
    private FlakeIdGenerator messageID;

    private MultiMap<String, String> jobsStarted;
    private boolean initialized = false;
    private boolean embedded = false;

    // Key is service name
    private final Map<String, HazelcastInstance> remoteHazelcasts = new HashMap<>();

    // Singleton implementation
    private MSFController() { /*this.init();*/ }
    private static class Singleton {
        private static final MSFController INSTANCE = new MSFController();
    }

    public static MSFController getInstance() {
        if (!Singleton.INSTANCE.initialized)
            throw new IllegalStateException("MSFController has not been initialized properly");
        return Singleton.INSTANCE;
    }

    public static MSFController getOrCreateInstance(boolean embedded, byte[] clientConfig) {
        if (!Singleton.INSTANCE.initialized) {
            return createInstance(embedded, clientConfig);
        } else {
            return getInstance();
        }
    }

    // Support for both embedded and client-server modes complicates the singleton initialization;
    // the service will initialize the controller on the client side, but server side elements
    // such as pipelines also access the controller where it is not initialized by the
    // service.  As a result, there are multiple points of initialization -- those should
    // call createInstance().  Objects like EventStores and DAOs should still call
    // getInstance(), as they are always called from either a Pipeline or a Service that
    // will have initialized the controller.
    public static MSFController createInstance(boolean embedded, byte[] clientConfig) {
        Singleton.INSTANCE.init(embedded, clientConfig);
        Singleton.INSTANCE.initialized = true;
        Singleton.INSTANCE.embedded = embedded;
        return Singleton.INSTANCE;
    }

    private void init(boolean embedded, byte[] clientConfig) {
        if (embedded) {
            Config config = new YamlConfigBuilder().build();
            config.getJetConfig().setEnabled(true);
            System.out.println("MSFController starting Hazelcast Platform embedded instance with config from classpath");
            hazelcast = Hazelcast.newHazelcastInstance(config);
        } else {
            InputStream is = new ByteArrayInputStream(clientConfig);
            ClientConfig config = new YamlClientConfigBuilder(is).build();

            // Doing programmatically for now since YAML marked invalid
            config.getSerializationConfig().getCompactSerializationConfig().setEnabled(true);
            System.out.println("MSFController has explicitly enabled compact serialization (can remove in 5.1)");

            System.out.println("MSFController starting Hazelcast Platform client with config from classpath");
            hazelcast = HazelcastClient.newHazelcastClient(config);
            System.out.println("              Target cluster: " + hazelcast.getConfig().getClusterName());

            // HZCE doesn't have GUI support for enabling Map Journal
            enableMapJournal();
        }
        messageID = hazelcast.getFlakeIdGenerator("messageID");
        jobsStarted = hazelcast.getMultiMap("servicesRunning");
    }

    public HazelcastInstance getHazelcastInstance() { return hazelcast; }

    // This needs to run on cluster, not on client, so need to submit this via Runnable/Callable
    private void enableMapJournal() {
        ExecutorService executor = hazelcast.getExecutorService("Executor");
        executor.submit(new MapJournalEnabler());
    }

    public static class MapJournalEnabler implements Runnable, Serializable, HazelcastInstanceAware {
        private transient HazelcastInstance hazelcast;

        @Override
        public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
            this.hazelcast = hazelcastInstance;
        }

        @Override
        public void run() {
            MapConfig jmconfig = new MapConfig();
            // TODO: should not be using 'Account' specific settings at this level - this is a temp
            //  workaround for not being able to enable MapJournal thru HZCE console.  If this needs
            //  to persist long-term, services will need to pass us patterns to use for journal
            //  enablement.
            jmconfig.setName("AccountEvent_*");
            jmconfig.getEventJournalConfig().setEnabled(true).setCapacity(100000);
            hazelcast.getConfig().addMapConfig(jmconfig);
            System.out.println("Enabled MapJournal for AccountEvent_*");
        }
    }

    public boolean isEmbedded() { return embedded; }

    // Probably an unnecessary level of indirection, but thought there might
    // be some configuration we'd do here around eviction, TTL, etc. that
    // would be managed by the framework and not the individual clients.
    public IMap getMap(String name) { return hazelcast.getMap(name); }
    public long getUniqueMessageID() { return messageID.newId(); }
    public IList getList(String name) { return hazelcast.getList(name); }
    public ICountDownLatch getCountDownLatch(String name) { return hazelcast.getCPSubsystem().getCountDownLatch(name); }

    // Event pub-sub support
    public Ringbuffer getRingbuffer(String name) {
        return hazelcast.getRingbuffer(name);
    }

    /** Publish will always be called by the 'local' service so we can use the
     * hazelcastInstance to retrieve it
     * @param topicName By convention, topic name is the name of the Event class
     * @param event the event to publish
     */
    @Deprecated
    public void publish(String topicName, SequencedEvent event) {
       ITopic topic = hazelcast.getReliableTopic(topicName);
       topic.publish(event);
       System.out.println("Published " + event + " to " + topicName);
    }

    /** Subscribe will usually be called for a remote service, so we need to open
     * a client connection to it.   We can cache these by service name.
     * @param serviceName
     * @param topic
     * @param listener
     */
    @Deprecated
    public void subscribe(String serviceName, String topic, EventMsgListener listener) {
        HazelcastInstance hz = remoteHazelcasts.get(serviceName);

        if (hz == null) {
            try {
                String cfgFile = "hazelcast-client-" + serviceName + ".yaml";
                ClientConfig remoteConfig = new YamlClientConfigBuilder(cfgFile).build();
                hz = HazelcastClient.newHazelcastClient(remoteConfig);
                remoteHazelcasts.put(serviceName, hz);
                System.out.println("Successful client connection to " + serviceName);
            } catch (IOException ioe) {
                ioe.printStackTrace();
                System.out.println("Subscribe to " + serviceName + " failed.");
                return;
            }
        }

        ITopic t = hz.getReliableTopic(topic);
        t.addMessageListener(listener);
        System.out.println("Subscribed to " + topic);

    }
    // Might move this to EventStore base class
    @Deprecated  // moved to EventStore
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
        jobsStarted.put(service, null);  // might not be legal
    }
    public void stopService(String service) {
        Collection<String> jobsRunning = jobsStarted.get(service);
        for (String job : jobsRunning) {
            hazelcast.getJet().getJob(job).cancel();
        }
        jobsStarted.remove(service);
        // TODO: use logger
        System.out.println("All jobs for " + service + " have initiated shutdown.");
    }

    // By moving job control out of the service, we can let the framework decide
    // (likely via configuration) whether we're doing embedded or client/server,
    // how many instances to start, etc.
    public void startJob(String service, String jobName, Pipeline p) {
        startJob(service, jobName, p, new URL[]{});
    }

    // Adding jars still results in Serialization errors on lambda functions within
    // createContextFn of services ... so dropping back to adding jars to platform classpath
    // (self-managed deploy) or uploading thru cloud console (managed service deploy) until I
    // can figure out why this fails.
    public void startJob(String service, String jobName, Pipeline p, URL[] jars) {
        JobConfig jconfig = new JobConfig();
        for (URL url : jars) {
            jconfig.addJar(url);
            System.out.println(" Added to JobConfig: " + url.toString());
        }
        jconfig.setName(jobName);

        System.out.println("MSFController starting job " + jobName);
        try {
            hazelcast.getJet().newJobIfAbsent(p, jconfig);
            jobsStarted.put(service, jobName);
            System.out.println(jobsStarted.get(service).size() + " jobs now running for " + service);
        } catch (Throwable t) {
            System.out.println("Job " + jobName + ": start failed ");
            t.printStackTrace();
        }
    }

    public SqlService getSqlService() { return hazelcast.getSql(); }

}
