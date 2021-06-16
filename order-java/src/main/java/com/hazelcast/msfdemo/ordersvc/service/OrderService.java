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

package com.hazelcast.msfdemo.ordersvc.service;

import com.hazelcast.map.IMap;
import com.hazelcast.msf.controller.MSFController;
import com.hazelcast.msfdemo.ordersvc.business.CreateOrderPipeline;
import com.hazelcast.msfdemo.ordersvc.business.InventoryReservePipeline;
import com.hazelcast.msfdemo.ordersvc.business.PriceLookupPipeline;
import com.hazelcast.msfdemo.ordersvc.domain.Order;
import com.hazelcast.msfdemo.ordersvc.eventstore.OrderEventStore;
import com.hazelcast.msfdemo.ordersvc.views.OrderDAO;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderService {

    MSFController controller;
    OrderDAO orderDAO;
    OrderEventStore eventStore;

    public void init() {
        controller = MSFController.getInstance();
        orderDAO = new OrderDAO();

        // Initialize the EventStore
        eventStore = OrderEventStore.getInstance();
        // TODO: should have process that occasionally snapshots & evicts

        // Start the various Jet transaction handler pipelines
        ExecutorService executor = Executors.newCachedThreadPool();
        CreateOrderPipeline orderPipeline = new CreateOrderPipeline(this);
        executor.submit(orderPipeline);

        PriceLookupPipeline pricePipeline = new PriceLookupPipeline(this);
        executor.submit(pricePipeline);

        InventoryReservePipeline reservePipeline = new InventoryReservePipeline(this);
        executor.submit(reservePipeline);

    }

    public OrderEventStore getEventStore() { return eventStore; }
    public IMap<String, Order> getView() { return orderDAO.getMap(); }

    public void shutdown() {
        // notify Hazelcast controller, it can shut down if no other
        // services are still running.
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        OrderService orderService = new OrderService();
        orderService.init();

        final GrpcServer server = new GrpcServer();
        server.start();
        server.blockUntilShutdown();
    }
}
