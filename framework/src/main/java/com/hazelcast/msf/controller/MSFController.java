package com.hazelcast.msf.controller;

import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.collection.IList;
import com.hazelcast.config.Config;
import com.hazelcast.config.ConfigXmlGenerator;
import com.hazelcast.config.EventJournalConfig;
import com.hazelcast.config.IndexConfig;
import com.hazelcast.config.IndexType;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.IAtomicLong;
import com.hazelcast.flakeidgen.FlakeIdGenerator;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.Job;
import com.hazelcast.jet.config.JetConfig;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.map.IMap;
import com.hazelcast.msf.configuration.ConfigUtil;
import com.hazelcast.topic.ITopic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MSFController {

    private HazelcastInstance hazelcast;
    private JetInstance jet;
    private FlakeIdGenerator messageID;
    //private Map<String, FlakeIdGenerator> idGenerators = new HashMap<>();
    private List<Job> runningJobs = new ArrayList<>();

    private Map<String, Class> servicesStarted;

    // Singleton implementation
    private MSFController() { this.init(); }
    private static class Singleton {
        private static final MSFController INSTANCE = new MSFController();
    }
    public static MSFController getInstance() {
        return Singleton.INSTANCE;
    }

    private void init() {
        // Hard coding to use embedded for now, but ConfigUtil has been
        // added to the project so I can start adapting it to the
        // needs of the framework
        String configname = "embedded";
        //String configname = ConfigUtil.findConfigNameInArgs(args);
        // Jet doesn't accept a clientConfig argument, only Config().
        ClientConfig clientConfig = ConfigUtil.getClientConfigForCluster(configname);

        Config config = new Config();
        // Wildcards don't seem to work here; need a service registration model
        // where we can register all the pipelines up front and then iterate
        // through their configs before we start the cluster.
        MapConfig mc = config.getMapConfig("AccountEvent_OPEN");
        MapConfig m2 = config.getMapConfig("AccountEvent_ADJUST");
        EventJournalConfig ejc = new EventJournalConfig();
        ejc.setEnabled(true);
        mc.setEventJournalConfig(ejc);
        m2.setEventJournalConfig(ejc);

        JetConfig jetConfig = new JetConfig();
        jetConfig.setHazelcastConfig(config);
        jet = Jet.newJetInstance(jetConfig); // TODO: make configurable whether embedded or client
        hazelcast = jet.getHazelcastInstance();
        messageID = hazelcast.getFlakeIdGenerator("messageID");
    }

    // Probably an unnecessary level of indirection, but thought there might
    // be some configuration we'd do here around eviction, TTL, etc. that
    // would be managed by the framework and not the individual clients.
    public IMap getMap(String name) { return hazelcast.getMap(name); }
    public ITopic getTopic(String name) { return hazelcast.getReliableTopic(name); }
    public long getUniqueMessageID() { return messageID.newId(); }
    public IList getList(String name) { return hazelcast.getList(name); }

    // Might move this to an EventStore base class
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
    public void startService(String service) {}
    public void stopService(String service) {}

    // By moving job control out of the service, we can let the framework decide
    // (likely via configuration) whether we're  doing embedded or client/server,
    // how many instances to start, etc.
    public void startJob(String name, Pipeline p) {
        JobConfig jconfig = new JobConfig();
        jconfig.setName(name);
        System.out.println("MSFController starting job " + name);
        try {
            Job j = jet.newJobIfAbsent(p, jconfig);
            runningJobs.add(j); // Maybe key by service so stopservice can cancel it?
            //System.out.println("Started and logged");
        } catch (Throwable t) {
            System.out.println("Job start failed " + t.getMessage());
        }
    }

}
