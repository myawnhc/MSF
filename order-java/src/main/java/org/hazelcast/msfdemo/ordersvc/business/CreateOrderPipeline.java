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

package org.hazelcast.msfdemo.ordersvc.business;

import com.hazelcast.jet.datamodel.Tuple2;
import com.hazelcast.jet.pipeline.JournalInitialPosition;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.ServiceFactories;
import com.hazelcast.jet.pipeline.ServiceFactory;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.Sources;
import com.hazelcast.jet.pipeline.StreamStage;
import com.hazelcast.map.IMap;
import org.hazelcast.msf.controller.MSFController;
import org.hazelcast.msf.messaging.APIResponse;
import org.hazelcast.msfdemo.ordersvc.domain.Order;
import org.hazelcast.msfdemo.ordersvc.domain.WaitingOn;
import org.hazelcast.msfdemo.ordersvc.events.CreateOrderEvent;
import org.hazelcast.msfdemo.ordersvc.events.OrderEvent;
import org.hazelcast.msfdemo.ordersvc.events.OrderEventTypes;
import org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreateOrderRequest;
import org.hazelcast.msfdemo.ordersvc.eventstore.OrderEventStore;
import org.hazelcast.msfdemo.ordersvc.service.OrderService;

import java.util.AbstractMap;
import java.util.EnumSet;

import static com.hazelcast.jet.datamodel.Tuple2.tuple2;

public class CreateOrderPipeline implements Runnable {

    private static OrderService service;

    public CreateOrderPipeline(OrderService service) {
        CreateOrderPipeline.service = service;
    }

    @Override
    public void run() {
        try {
            MSFController controller = MSFController.getInstance();
            System.out.println("CreateOrderPipeline.run() invoked, submitting job");
            controller.startJob("OrderService", "OrderService.CreateOrder", createPipeline());
        } catch (Exception e) { // Happens if our pipeline is not valid
            e.printStackTrace();
        }
    }

    private static Pipeline createPipeline() {
        Pipeline p = Pipeline.create();
        String requestMapName = OrderEventTypes.CREATE.getQualifiedName();
        IMap<Long, CreateOrderRequest> requestMap = MSFController.getInstance().getMap(requestMapName);
        String responseMapName = requestMapName + ".Results";
        IMap<Long, APIResponse<?>> responseMap = MSFController.getInstance().getMap(responseMapName);
        // Kind of a pain that we have to propagate the request ID throughout the entire
        // pipeline but don't want to pollute domain objects with it.
        StreamStage<Tuple2<Long, CreateOrderEvent>> tupleStream = p.readFrom(Sources.mapJournal(requestMap,
                JournalInitialPosition.START_FROM_OLDEST))
                .withIngestionTimestamps()
                .setName("Read from " + requestMapName)
                // Not needed: filter - here a nop.
                // Not needed: transform - handle versioning, nop for now
                // Not needed: enrich - nothing to do for order creation
                // Create OrderEvent object
                .map(entry -> {
                    //System.out.println("Creating OrderEvent, returning Tuple2");
                    Long uniqueRequestID = entry.getKey();
                    CreateOrderRequest request = entry.getValue();
                    long orderNumber = MSFController.getInstance().getUniqueId("orderNumber");
                    CreateOrderEvent event = new CreateOrderEvent(
                            ""+orderNumber, request.getAccountNumber(), request.getItemNumber(),
                                        request.getLocation(), request.getQuantity());
                    return tuple2(uniqueRequestID, event);
                })
                .setName("Create OrderEvent.CREATE");

        // Peek in on progress
//        tupleStream.window(oneSecond)
//                .aggregate(AggregateOperations.counting())
//                .setName("Count operations per second")
//                .writeTo(Sinks.logger(count -> "OrderEvent.CREATE count " + count));

        // Persist to Event Store and Materialized View
        ServiceFactory<?, OrderEventStore> eventStoreServiceFactory =
                ServiceFactories.sharedService(
                        (ctx) -> OrderEventStore.getInstance()
                );

        ServiceFactory<?,IMap<String, Order>> materializedViewServiceFactory = ServiceFactories.iMapService(service.getView().getName());


        tupleStream.mapUsingService( eventStoreServiceFactory, (eventStore, tuple) -> {
                    eventStore.append(tuple.f1());
                    //System.out.println("CreateOrderEvent persisted");
                    return tuple;
                }).setName("Persist CreateOrderEvent to event store")

        // Create Materialized View object and publish it
        .mapUsingService(materializedViewServiceFactory, (viewMap, tuple)-> {
            //System.out.println("Creating Account (Materialized View) object");
            CreateOrderEvent orderEvent = tuple.f1();
            Order o = new Order();
            o.setOrderNumber(orderEvent.getOrderNumber());
            o.setAcctNumber(orderEvent.getAccountNumber());
            o.setItemNumber(orderEvent.getItemNumber());
            o.setLocation(orderEvent.getLocation());
            o.setQuantity(orderEvent.getQuantity());
            o.setExtendedPrice(0);
            o.setWaitingOn(EnumSet.of(WaitingOn.PRICE_LOOKUP));
            viewMap.put(orderEvent.getOrderNumber(), o);
            //System.out.println("View object created and published");
            return tuple2(tuple.f0(), orderEvent);
        }).setName("Create and publish Order Materialized View")

        .map( tuple -> {
            Long uniqueID = tuple.f0();
            OrderEvent event = tuple.f1();

            APIResponse<OrderEvent> response = new APIResponse<>(uniqueID, event);
            //System.out.println("Building and returning API response");
            return new AbstractMap.SimpleEntry<>(uniqueID, response);
        }).setName("Build client APIResponse")
                .writeTo(Sinks.map(responseMap))
                .setName("Send response to client");

        return p;
    }
}
