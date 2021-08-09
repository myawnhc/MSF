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
import com.hazelcast.msfdemo.ordersvc.business.CollectPaymentPipeline;
import com.hazelcast.msfdemo.ordersvc.business.CreateOrderPipeline;
import com.hazelcast.msfdemo.ordersvc.business.CreditCheckPipeline;
import com.hazelcast.msfdemo.ordersvc.business.InventoryReservePipeline;
import com.hazelcast.msfdemo.ordersvc.business.PriceLookupPipeline;
import com.hazelcast.msfdemo.ordersvc.dashboard.PumpGrafanaStats;
import com.hazelcast.msfdemo.ordersvc.domain.Order;
import com.hazelcast.msfdemo.ordersvc.eventstore.OrderEventStore;
import com.hazelcast.msfdemo.ordersvc.views.OrderDAO;
import com.hazelcast.scheduledexecutor.DuplicateTaskException;
import com.hazelcast.scheduledexecutor.IScheduledExecutorService;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public class OrderService {

    private MSFController controller;
    private OrderDAO orderDAO;
    private OrderEventStore eventStore;
    public static final String SERVICE_NAME = "OrderService";
    private static final String GRAFANA_HOST = "grafana.host";

    public void init() {
        controller = MSFController.getInstance();
        orderDAO = new OrderDAO();

        // Initialize the EventStore
        eventStore = OrderEventStore.getInstance();
        // TODO: should have process that occasionally snapshots & compacts

        // Start Grafana first, otherwise (for low order counts) we miss the first stages
        // of order processing.
        // Grafana needs to run in-cluster to minimize latency.
        // grafana is docker container name; if running bare metal, add in /etc/hosts as localhost alias
        String grafanaHost = System.getProperty(GRAFANA_HOST, "grafana");
        //System.out.println("** Grafana host: " + grafanaHost);
        PumpGrafanaStats pgs = new PumpGrafanaStats(grafanaHost, orderDAO);
        IScheduledExecutorService dses = controller.getScheduledExecutorService("scheduledExecutor");
        try {
            dses.scheduleAtFixedRate(pgs, 1, 5, TimeUnit.SECONDS);
        } catch (DuplicateTaskException dte) {
            ; // OK to ignore
        } catch (RejectedExecutionException ree) {
            System.out.println("PumpGrafanaStats scheduled execution rejected");
        }

        // Start the various Jet transaction handler pipelines
        ExecutorService executor = Executors.newFixedThreadPool(7);
        CreateOrderPipeline orderPipeline = new CreateOrderPipeline(this);
        executor.submit(orderPipeline);

        PriceLookupPipeline pricePipeline = new PriceLookupPipeline(this);
        executor.submit(pricePipeline);

        InventoryReservePipeline reservePipeline = new InventoryReservePipeline(this);
        executor.submit(reservePipeline);

        CreditCheckPipeline creditCheckPipeline = new CreditCheckPipeline(this);
        executor.submit(creditCheckPipeline);

        CollectPaymentPipeline collectPaymentPipeline = new CollectPaymentPipeline(this);
        executor.submit(collectPaymentPipeline);

        // TODO: PullInventory pipeline to come.  Ship will just be a final status, not a pipeline.
    }

    public OrderEventStore getEventStore() { return eventStore; }
    public OrderDAO getDAO() { return orderDAO; }
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
