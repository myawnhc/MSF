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

package org.hazelcast.msfdemo.ordersvc.service;

import com.hazelcast.map.IMap;
import org.hazelcast.msf.configuration.ServiceConfig;
import org.hazelcast.msf.controller.MSFController;
import org.hazelcast.msfdemo.ordersvc.business.CollectPaymentPipeline;
import org.hazelcast.msfdemo.ordersvc.business.CreateOrderPipeline;
import org.hazelcast.msfdemo.ordersvc.business.CreditCheckPipeline;
import org.hazelcast.msfdemo.ordersvc.business.InventoryReservePipeline;
import org.hazelcast.msfdemo.ordersvc.business.PriceLookupPipeline;
import org.hazelcast.msfdemo.ordersvc.business.PullInventoryPipeline;
import org.hazelcast.msfdemo.ordersvc.business.ShipPipeline;
import org.hazelcast.msfdemo.ordersvc.domain.Order;
import org.hazelcast.msfdemo.ordersvc.eventstore.OrderEventStore;
import org.hazelcast.msfdemo.ordersvc.views.OrderDAO;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderService {

    private MSFController controller;
    private OrderDAO orderDAO;
    private OrderEventStore eventStore;
    public static final String SERVICE_NAME = "OrderService";
    private boolean embedded;
    private byte[] clientConfig;

    public void init(boolean isEmbedded, byte[] clientConfig) {
        this.embedded = isEmbedded;
        this.clientConfig = clientConfig;
        if (!embedded && clientConfig == null) {
            throw new IllegalArgumentException("ClientConfig cannot be null for client-server deployment");
        }
        controller = MSFController.createInstance(isEmbedded, clientConfig);
        orderDAO = new OrderDAO(controller);

        // Initialize the EventStore
        eventStore = new OrderEventStore(controller.getHazelcastInstance());
        // TODO: should have process that occasionally snapshots & compacts

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

        PullInventoryPipeline pullInventoryPipeline = new PullInventoryPipeline(this);
        executor.submit(pullInventoryPipeline);

        ShipPipeline shipPipeline = new ShipPipeline(this);
        executor.submit(shipPipeline);

    }

    public boolean isEmbedded() { return embedded; }
    public byte[] getClientConfig() { return clientConfig; }

    public OrderEventStore getEventStore() { return eventStore; }
    public OrderDAO getDAO() { return orderDAO; }
    public IMap<String, Order> getView() { return orderDAO.getMap(); }
    public IMap getMap(String name) { return controller.getMap(name);}

    public void shutdown() {
        // notify Hazelcast controller, it can shut down if no other
        // services are still running.
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        ServiceConfig.ServiceProperties props = ServiceConfig.get("order-service");
        OrderService orderService = new OrderService();
        orderService.init(props.isEmbedded(), props.getClientConfig());

        final GrpcServer server = new GrpcServer();
        server.start();
        server.blockUntilShutdown();
    }
}
