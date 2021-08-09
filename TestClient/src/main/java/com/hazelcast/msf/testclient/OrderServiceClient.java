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

package com.hazelcast.msf.testclient;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.hazelcast.msf.configuration.ServiceConfig;
import com.hazelcast.msfdemo.ordersvc.events.OrderGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.*;

public class OrderServiceClient {
    private static final Logger logger = Logger.getLogger(OrderServiceClient.class.getName());
    private final OrderGrpc.OrderBlockingStub blockingStub; // unused
    private final OrderGrpc.OrderFutureStub futureStub;
    private final OrderGrpc.OrderStub asyncStub;

    private List<String> validAccounts;

    public static void main(String[] args) {
        // Access a service running on the local machine on port 50052
        //String target = "localhost:50052";
        ServiceConfig.ServiceProperties props = ServiceConfig.get("order-service");
        String target = props.getTarget();
        logger.info("Target from service.yaml.test " + target);

        // Create a communication channel to the server, known as a Channel. Channels are thread-safe
        // and reusable. It is common to create channels at the beginning of your application and reuse
        // them until the application shuts down.
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                .usePlaintext()
                .build();

        try {
            OrderServiceClient orderServiceClient = new OrderServiceClient(channel);
            orderServiceClient.nonBlockingOrder();
        } finally {

        }

        // Could wait for all orders to be fully processed and then exit (perhaps
        // by incrementing a counter in OrderEventResponseProcessor.onCompleted() and
        // exiting when it equals number of orders placed).  For now, we just hold
        // until interrupted.
        while (true) {
            try {
                Thread.sleep(60_000);
            } catch (InterruptedException e) {

            }
        }

    }

    /** Construct client for accessing server using the existing channel. */
    public OrderServiceClient(Channel channel) {
        // 'channel' here is a Channel, not a ManagedChannel, so it is not this code's responsibility to
        // shut it down.

        // Passing Channels to code makes code easier to test and makes it easier to reuse Channels.
        blockingStub = OrderGrpc.newBlockingStub(channel);
        futureStub = OrderGrpc.newFutureStub(channel);
        asyncStub = OrderGrpc.newStub(channel);

        AccountServiceClient asc = new AccountServiceClient();
        // Expect no accounts initially, but check in case we later make account info
        // persistent.
        try {
            validAccounts = asc.getAllAccountNumbers();
            if (validAccounts.size() == 0) {
                logger.info("Initializing 1000 test accounts");
                asc.openTestAccounts(1000);
                validAccounts = asc.getAllAccountNumbers();
                logger.info("After test data init, have " + validAccounts.size() + " accounts");
            }
        } catch (io.grpc.StatusRuntimeException e) {
            try {
                logger.info("Waiting for acctsvc to become ready");
                Thread.sleep(1000);
            } catch (InterruptedException interruptedException) {
            }
        }
    }

    private static class OrderEventResponseProcessor implements StreamObserver<OrderEventResponse> {

        @Override
        public void onNext(OrderEventResponse orderEventResponse) {
            System.out.println("Received response: " + formatResponse(orderEventResponse));
        }

        @Override
        public void onError(Throwable throwable) {
            System.out.println("onError " + throwable);
        }

        @Override
        public void onCompleted() { }
    }

    static String formatResponse(OrderEventResponse response) {
        return response.getEventName() + " O:" + response.getOrderNumber() +
                " A:" + response.getAccountNumber() +
                " I: " + response.getItemNumber() +
                " L: " + response.getLocation() +
                " Q: " + response.getQuantity() +
                " $: " + response.getExtendedPrice();
    }

    public void nonBlockingOrder()  {
        //logger.info("Starting OSC.nonBlockingOrder");

        // Data generation for inventory might be happening concurrently with the
        // client sending in orders; we'd like to avoid sending orders for items
        // whose inventory records have not yet been created.  Since we generate
        // them sequentially, we can use the size of the inventory map to know
        // what the maximum 'safe' item number is.  When we scale this up, we
        // should periodically refresh the invRecordCount until it reaches
        // max (items * locations, currently 100K)
        //List<ListenableFuture<CreateOrderResponse>> futures = new ArrayList<>();

        InventoryServiceClient iclient = new InventoryServiceClient();
        int invRecordCount = iclient.getInventoryRecordCount();
        System.out.println("Starting to place orders, inventory record count " + invRecordCount);
        int NUM_LOCATIONS = 100;
        int maxSafeItem = invRecordCount / NUM_LOCATIONS;
        // Bumping order count from 10 to 1K
        for (int i=0; i<30; i++) {
            int index = (int)(Math.random()*validAccounts.size());
            String acctNumber = validAccounts.get(index);
            int itemOffset = (int)(Math.random()*maxSafeItem+1);
            int locationNum = (int)(Math.random()*NUM_LOCATIONS+1);
            String itemNumber = ""+(10101+itemOffset);
            String location = locationNum < 10 ? "W" + locationNum : "S" + locationNum;
            //System.out.println("Account at index " + index + " is " + acctNumber);
             CreateOrderRequest request = CreateOrderRequest.newBuilder()
                    .setAccountNumber(acctNumber)
                    .setItemNumber(itemNumber)
                    .setQuantity(1)
                    .setLocation(location)
                    .build();
            try {
                Executor e = Executors.newCachedThreadPool();
                //futures.add(futureStub.createOrder(request));
                ListenableFuture<CreateOrderResponse> future = futureStub.createOrder(request);
                Futures.addCallback(future, new FutureCallback<CreateOrderResponse>() {

                    @Override
                    public void onSuccess(@NullableDecl CreateOrderResponse createOrderResponse) {
                        System.out.println("Order created: " + createOrderResponse.getOrderNumber());

                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        System.out.println("Order creation failed: " + throwable.getMessage());

                    }
                }, e);
                // When changed to server-side streaming RPC, createOrder disappeared from futureStub!
                //asyncStub.createOrder(request, new OrderEventResponseProcessor());
                System.out.println("Placed order " + i);
            } catch (StatusRuntimeException e) {
                logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
                return;
            }
        }
//        // await all
//        // TODO: should do this more incrementally, not wait for everything.
//        List<CreateOrderResponse> responses;
//        try {
//            responses = lf.get();
//            for (CreateOrderResponse response : responses) {
//                System.out.println("Order created: " + response.getOrderNumber());
//            }
//        } catch(InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        }

        return;
    }
}
