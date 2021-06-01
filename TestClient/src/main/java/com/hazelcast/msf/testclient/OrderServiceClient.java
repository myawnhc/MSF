package com.hazelcast.msf.testclient;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.hazelcast.msfdemo.ordersvc.events.OrderGrpc;
import com.hazelcast.msfdemo.ordersvc.events.OrderOuterClass;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderServiceClient {
    private static final Logger logger = Logger.getLogger(OrderServiceClient.class.getName());
    private final OrderGrpc.OrderBlockingStub blockingStub;
    private final OrderGrpc.OrderFutureStub futureStub;

    public static void main(String[] args) {
        // Access a service running on the local machine on port 50052
        String target = "localhost:50052"; // TODO: get from a config file

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
        } finally {

        }
    }

    /** Construct client for accessing server using the existing channel. */
    public OrderServiceClient(Channel channel) {
        // 'channel' here is a Channel, not a ManagedChannel, so it is not this code's responsibility to
        // shut it down.

        // Passing Channels to code makes code easier to test and makes it easier to reuse Channels.
        blockingStub = OrderGrpc.newBlockingStub(channel);
        futureStub = OrderGrpc.newFutureStub(channel);
    }

    public void nonBlockingOrder()  {
        List<ListenableFuture<OrderOuterClass.CreateOrderResponse>> futures = new ArrayList<>();
        for (int i=0; i<10; i++) {
            // TODO: Because we use flake ids for acct numbers we can't guess them,
            //       probably need to do an AccountDAO query to get them.
            // TODO: Inventory service not written yet but probably has the same issue
            // TODO: Locations can be simple formula like Store001-Store099, Warehouse01-Warehouse09
            //String name = "Acct " + prefix + i;
            //int balance = Double.valueOf(Math.random()*10000).intValue()*100;
            OrderOuterClass.CreateOrderRequest request = OrderOuterClass.CreateOrderRequest.newBuilder()
                    .setAccountNumber("1")
                    .setItemNumber("1")
                    .setQuantity(1)
                    .setLocation("1")
                    .build();
            try {
                futures.add(futureStub.createOrder(request));
            } catch (StatusRuntimeException e) {
                logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
                return;
            }
        }

        try {
            // Can use successfulAsList rather than allAsList to get only good responses
            ListenableFuture<List<OrderOuterClass.CreateOrderResponse>> responseList = Futures.allAsList(futures);
            List<OrderOuterClass.CreateOrderResponse> responses = responseList.get();
            //List<String> openedAccountNumbers = new ArrayList<>();
            //System.out.println("Successful response count for group " + prefix + " = " + responses.size());
            for (OrderOuterClass.CreateOrderResponse oar : responses) {
                boolean succeeded = oar.getSucceeded();
                // Count them?  Do what with responses?
            }
            return;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return;
    }
}
