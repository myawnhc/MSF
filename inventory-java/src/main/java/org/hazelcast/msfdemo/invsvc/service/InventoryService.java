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

package org.hazelcast.msfdemo.invsvc.service;

import org.hazelcast.msf.configuration.ServiceConfig;
import org.hazelcast.msf.controller.MSFController;
import org.hazelcast.msfdemo.invsvc.business.CDCPipeline;
import org.hazelcast.msfdemo.invsvc.events.InventoryEventStore;
import org.hazelcast.msfdemo.invsvc.testdata.GenerateData;
import org.hazelcast.msfdemo.invsvc.views.InventoryDAO;
import org.hazelcast.msfdemo.invsvc.views.ItemDAO;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InventoryService {

    private MSFController controller;
    private ItemDAO itemDAO;
    private InventoryDAO inventoryDAO;
    private InventoryEventStore eventStore;
    public static final String SERVICE_NAME = "InventoryService";
    private boolean embedded;
    private URL clientConfigURL;

    private void init(boolean isEmbedded, byte[] clientConfig) {
        controller = MSFController.createInstance(isEmbedded, clientConfig);
        inventoryDAO = new InventoryDAO(controller);
        itemDAO = new ItemDAO(controller);

        // Initialize the EventStore
        eventStore = InventoryEventStore.getInstance();
        // TODO: should have process that occasionally snapshots & evicts

        // Start the various Jet transaction handler pipelines
        ExecutorService executor = Executors.newCachedThreadPool();
        CDCPipeline cdcPipeline = new CDCPipeline();
        executor.submit(cdcPipeline);

        // Item and Inventory data is persistent, but if we're running the first time
        // after pulling a new DB image we'll need to generate our test data
        executor.submit(() -> {
            GenerateData generator = new GenerateData(controller);
            if (itemDAO.getItemCount() == 0) {
                System.out.println("InventoryService - items empty, generating");
                generator.generateItems(1000);
            }
            if (inventoryDAO.getInventoryRecordCount() == 0) {
                System.out.println("InventoryService - inventory empty, generating");
                generator.generateInventory(1000, 100, 10);
            }
        });

    }

    public static void main(String[] args) throws IOException, InterruptedException {
        ServiceConfig.ServiceProperties props = ServiceConfig.get("inventory-service");
        InventoryService inventoryService = new InventoryService();
        inventoryService.init(props.isEmbedded(), props.getClientConfig());

        final GrpcServer server = new GrpcServer();
        server.start();
        server.blockUntilShutdown();
    }
}
