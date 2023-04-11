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

package org.hazelcast.msfdemo.acctsvc.service;

import com.hazelcast.map.IMap;
import org.hazelcast.msf.configuration.ServiceConfig;
import org.hazelcast.msf.controller.MSFController;
import org.hazelcast.msfdemo.acctsvc.business.AdjustBalancePipeline;
import org.hazelcast.msfdemo.acctsvc.business.OpenAccountPipeline;
import org.hazelcast.msfdemo.acctsvc.domain.Account;
import org.hazelcast.msfdemo.acctsvc.eventstore.AccountEventStore;
import org.hazelcast.msfdemo.acctsvc.views.AccountDAO;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AccountService {

    // Might refactor out MSFController and just hold HazelcastInstance here
    private MSFController controller;
    private AccountDAO accountDAO;
    private AccountEventStore eventStore;

    private boolean embedded;
    private byte[] clientConfig;

    public void init(boolean isEmbedded, byte[] clientConfig) {
        this.embedded = isEmbedded;
        this.clientConfig = clientConfig;
        if (!embedded && clientConfig == null) {
            throw new IllegalArgumentException("ClientConfig cannot be null for client-server deployment");
        }

        ClassLoader classLoader = AccountService.class.getClassLoader();
        Properties props = null;
        URL keystorePath = classLoader.getResource("client.keystore");
        if (keystorePath != null) {
            props = new Properties();
            System.out.println(" KeyStore Resource path: " + keystorePath);
            props.setProperty("javax.net.ssl.keyStore", "client.keystore");
            System.out.println("WARNING: TODO: hardcoded keystore password, should read from service.yaml");
            props.setProperty("javax.net.ssl.keyStorePassword", "2ec95573367");
        } else System.out.println(" null keystorePath");
        URL truststorePath = classLoader.getResource("client.truststore");
        if (truststorePath != null) {
            if (props == null) props = new Properties();
            System.out.println(" Truststore Resource path: " + truststorePath);
            props.setProperty("javax.net.ssl.trustStore", "client.truststore");
            props.setProperty("javax.net.ssl.trustStorePassword", "2ec95573367");
        } else System.out.println(" null truststorePath");


        controller = MSFController.createInstance("AccountService", isEmbedded, clientConfig, props);
        // DAO not currently being used but will be back as a Materialized View ...
        accountDAO = new AccountDAO(controller);

        // Initialize the EventStore
        eventStore = new AccountEventStore(controller.getHazelcastInstance());
        // TODO: should have process that occasionally snapshots & evicts

        // Start the various Jet transaction handler pipelines
        ExecutorService executor = Executors.newCachedThreadPool();
        OpenAccountPipeline openPipeline = new OpenAccountPipeline(this);
        try {
            executor.submit(openPipeline);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        AdjustBalancePipeline adjPipeline = new AdjustBalancePipeline(this);
        try {
            executor.submit(adjPipeline);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public boolean isEmbedded() { return embedded; }
    public byte[] getClientConfig() { return clientConfig; }

    public AccountEventStore getEventStore() { return eventStore; }
    public IMap<String, Account> getView() { return accountDAO.getMap(); }

    public void shutdown() {
        // notify Hazelcast controller, it can shut down if no other
        // services are still running.
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        ServiceConfig.ServiceProperties props = ServiceConfig.get("account-service");
        AccountService acctService = new AccountService();
        acctService.init(props.isEmbedded(), props.getClientConfig());

        final GrpcServer server = new GrpcServer();
        server.start();
        server.blockUntilShutdown();
    }
}
