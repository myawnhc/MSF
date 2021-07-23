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

package com.hazelcast.msfdemo.invsvc.service;

import com.hazelcast.msf.controller.MSFController;
import com.hazelcast.msfdemo.invsvc.business.CDCPipeline;
import com.hazelcast.msfdemo.invsvc.events.InventoryEventStore;
import com.hazelcast.msfdemo.invsvc.testdata.GenerateData;
import com.hazelcast.msfdemo.invsvc.views.InventoryDAO;
import com.hazelcast.msfdemo.invsvc.views.ItemDAO;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InventoryService {

    MSFController controller;
    ItemDAO itemDAO;
    InventoryDAO inventoryDAO;
    InventoryEventStore eventStore;

    private void init() {
        controller = MSFController.getInstance();
        inventoryDAO = new InventoryDAO();
        itemDAO = new ItemDAO();

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
            GenerateData generator = new GenerateData();
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
        InventoryService inventoryService = new InventoryService();
        inventoryService.init();

        final GrpcServer server = new GrpcServer();
        server.start();
        server.blockUntilShutdown();
    }
}
