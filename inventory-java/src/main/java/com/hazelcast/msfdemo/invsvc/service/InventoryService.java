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
import com.hazelcast.msfdemo.invsvc.events.InventoryEventStore;
import com.hazelcast.msfdemo.invsvc.views.InventoryDAO;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InventoryService {

    MSFController controller;
    InventoryDAO inventoryDAO;
    InventoryEventStore eventStore;

    private void init() {
        controller = MSFController.getInstance();
        inventoryDAO = new InventoryDAO();

        // Initialize the EventStore
        eventStore = InventoryEventStore.getInstance();
        // TODO: should have process that occasionally snapshots & evicts

        // Start the various Jet transaction handler pipelines
        ExecutorService executor = Executors.newCachedThreadPool();
        // ReserveInventoryPipeline
        // PullInventoryPipeline
        // full implementation would have add, remove, move
        // Probably ATP as a gRPC service because DAO doesn't help for non-local user

//        CreateOrderPipeline orderPipeline = new CreateOrderPipeline(this);
//        executor.submit(orderPipeline);

    }

    public static void main(String[] args) throws IOException, InterruptedException {
        InventoryService inventoryService = new InventoryService();
        inventoryService.init();

        final GrpcServer server = new GrpcServer();
        server.start();
        server.blockUntilShutdown();
    }
}
