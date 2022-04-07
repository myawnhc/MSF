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

package org.hazelcast.msfdemo.ordersvc.business;

import com.hazelcast.map.IMap;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.query.Predicates;
import io.grpc.stub.StreamObserver;
import org.hazelcast.msf.controller.MSFController;
import org.hazelcast.msf.messaging.APIResponse;
import org.hazelcast.msfdemo.ordersvc.events.ChargeAccountEvent;
import org.hazelcast.msfdemo.ordersvc.events.CreateOrderEvent;
import org.hazelcast.msfdemo.ordersvc.events.CreditCheckEvent;
import org.hazelcast.msfdemo.ordersvc.events.InventoryReserveEvent;
import org.hazelcast.msfdemo.ordersvc.events.OrderEvent;
import org.hazelcast.msfdemo.ordersvc.events.OrderEventTypes;
import org.hazelcast.msfdemo.ordersvc.events.OrderGrpc;
import org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass;
import org.hazelcast.msfdemo.ordersvc.events.OrderShippedEvent;
import org.hazelcast.msfdemo.ordersvc.events.PriceLookupEvent;
import org.hazelcast.msfdemo.ordersvc.events.PullInventoryEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Server-side implementation of the OrderService API
 *  Takes requests and puts them to API-specific IMaps that trigger Jet pipelines
 *  Looks for result in corresponding result map to return to client
 */
public class OrderAPIImpl extends OrderGrpc.OrderImplBase {

    final MSFController controller = MSFController.getInstance();

    // CREATE
    final String createRequestMapName = OrderEventTypes.CREATE.getQualifiedName();
    final IMap<Long, OrderOuterClass.CreateOrderRequest> orderPipelineInput = controller.getMap(createRequestMapName);
    final String createResponseMapName = createRequestMapName + ".Results";
    final IMap<Long, APIResponse<String>> orderPipelineOutput = controller.getMap(createResponseMapName);

    final private Map<Long, UUID> listenersByRequestID = new HashMap<>();

    public OrderAPIImpl() {
        // TODO: pull init of these out of pipelines and do it just once here for all of them
        ChargeAccountEvent.setHazelcastInstance(controller.getHazelcastInstance());
        CreateOrderEvent.setHazelcastInstance(controller.getHazelcastInstance());
        CreditCheckEvent.setHazelcastInstance(controller.getHazelcastInstance());
        InventoryReserveEvent.setHazelcastInstance(controller.getHazelcastInstance());
        OrderShippedEvent.setHazelcastInstance(controller.getHazelcastInstance());
        PriceLookupEvent.setHazelcastInstance(controller.getHazelcastInstance());
        PullInventoryEvent.setHazelcastInstance(controller.getHazelcastInstance());
    }


    @Override
    public void createOrder(OrderOuterClass.CreateOrderRequest request, StreamObserver<OrderOuterClass.CreateOrderResponse> responseObserver) {
        // Unique ID used to pair up requests with responses
        long uniqueID = controller.getUniqueMessageID();

        // Get listener to result map armed before we trigger the pipeline
        UUID listenerID = orderPipelineOutput.addEntryListener((EntryAddedListener<Long, APIResponse<OrderEvent>>) entryEvent -> {
            //System.out.println("OPEN completion listener fired for ID " + uniqueID);
            APIResponse<OrderEvent> apiResponse = entryEvent.getValue();
            if (apiResponse.getStatus() == APIResponse.Status.SUCCESS) {
                OrderEvent event = apiResponse.getResultValue();
                OrderOuterClass.CreateOrderResponse grpcResponse =
                        OrderOuterClass.CreateOrderResponse.newBuilder()
                                .setOrderNumber(event.getOrderNumber())
                                .build();
                //System.out.println("OrderAPIImpl sending create order response from APIResponse");
                responseObserver.onNext(grpcResponse);
                // Handle additional events for the order
                //new EventHandler(event.getOrderNumber(), responseObserver);
            } else {
                responseObserver.onError(apiResponse.getError());
            }
            // TODO: may send a stream of responses, in which case we do not want to
            // mark completed until the order actually ships ...

            responseObserver.onCompleted();
            orderPipelineOutput.remove(uniqueID);
            orderPipelineInput.remove(uniqueID);
            // Remove ourself as a listener.  Have to do this indirection of getting from map because
            // otherwise we get 'listenerID may not have been initialized'.
            UUID myID = listenersByRequestID.remove(uniqueID);
            if (myID == null) {
                System.out.println("OrderAPIImpl.create handler - listener for " + uniqueID + " was already removed");
            } else {
                orderPipelineOutput.removeEntryListener(myID);
            }

        }, Predicates.sql("__key=" + uniqueID), true);

        UUID oldID = listenersByRequestID.put(uniqueID, listenerID);
        if (oldID != null)
            System.out.println("ERROR: Multiple requests with same ID! " + oldID + " (seen in open)");
        // Pass the request into the OpenAccountHandler pipeline
        orderPipelineInput.set(uniqueID, request);
    }

    //rpc SubscribeToOrderCreated (SubscribeRequest) returns (stream OrderCreated) {}
    @Override
    public void subscribeToOrderCreated(OrderOuterClass.SubscribeRequest request,
                                        StreamObserver<OrderOuterClass.OrderCreated> responseObserver) {
        // request is an empty type
        CreateOrderEvent.setHazelcastInstance(controller.getHazelcastInstance());
        CreateOrderEvent.subscribe(responseObserver);
    }

    @Override
    public void subscribeToOrderShipped(OrderOuterClass.SubscribeRequest request,
                                        StreamObserver<OrderOuterClass.OrderShipped> responseObserver) {
        // request is an empty type
        OrderShippedEvent.setHazelcastInstance(controller.getHazelcastInstance());
        OrderShippedEvent.subscribe(responseObserver);
    }
}