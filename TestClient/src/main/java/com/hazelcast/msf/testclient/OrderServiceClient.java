package com.hazelcast.msf.testclient;

import com.hazelcast.msf.configuration.ServiceConfig;
import com.hazelcast.msfdemo.acctsvc.domain.Account;
import com.hazelcast.msfdemo.acctsvc.views.AccountDAO;
import com.hazelcast.msfdemo.ordersvc.events.OrderGrpc;
import com.hazelcast.msfdemo.ordersvc.events.OrderOuterClass;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderServiceClient {
    private static final Logger logger = Logger.getLogger(OrderServiceClient.class.getName());
    private final OrderGrpc.OrderBlockingStub blockingStub; // unused
    private final OrderGrpc.OrderFutureStub futureStub;
    private final OrderGrpc.OrderStub asyncStub; // unused

    private List<Account> validAccounts;

    public static void main(String[] args) {
        // Access a service running on the local machine on port 50052
        //String target = "localhost:50052";
        ServiceConfig.ServiceProperties props = ServiceConfig.get("order-service");
        String target = props.getTarget();
        logger.info("Target from service.yaml " + target);

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
    }

    /** Construct client for accessing server using the existing channel. */
    public OrderServiceClient(Channel channel) {
        // 'channel' here is a Channel, not a ManagedChannel, so it is not this code's responsibility to
        // shut it down.

        // Passing Channels to code makes code easier to test and makes it easier to reuse Channels.
        blockingStub = OrderGrpc.newBlockingStub(channel);
        futureStub = OrderGrpc.newFutureStub(channel);
        asyncStub = OrderGrpc.newStub(channel);

        Collection<Account> ac = new AccountDAO().getAllAccounts();
        validAccounts = new ArrayList(ac);
        System.out.println("Retrieved " + validAccounts.size() + " accounts from AccountDAO");
    }

    private static class OrderEventResponseProcessor implements StreamObserver<OrderOuterClass.OrderEventResponse> {

        @Override
        public void onNext(OrderOuterClass.OrderEventResponse orderEventResponse) {
            System.out.println(formatResponse(orderEventResponse));
        }

        @Override
        public void onError(Throwable throwable) {
            System.out.println("onError " + throwable);
        }

        @Override
        public void onCompleted() { }
    }

    static String formatResponse(OrderOuterClass.OrderEventResponse response) {
        return response.getEventName() + " O:" + response.getOrderNumber() +
                " A:" + response.getAccountNumber() +
                " I: " + response.getItemNumber() +
                " L: " + response.getLocation() +
                " Q: " + response.getQuantity() +
                " $: " + response.getExtendedPrice();
    }

    public void nonBlockingOrder()  {
        //ExecutorService pool = Executors.newCachedThreadPool() ;
        //List<ListenableFuture<OrderOuterClass.CreateOrderResponse>> futures = new ArrayList<>();
        for (int i=0; i<10; i++) {
            int index = (int)(Math.random()*validAccounts.size()+1);
            String acctNumber = validAccounts.get(index).getAcctNumber();
            //System.out.println("Account at index " + index + " is " + acctNumber);
             OrderOuterClass.CreateOrderRequest request = OrderOuterClass.CreateOrderRequest.newBuilder()
                    .setAccountNumber(acctNumber)
                    .setItemNumber("1")
                    .setQuantity(1)
                    .setLocation("1")
                    .build();
            try {
                // When changed to server-side streaming RPC, createOrder disappeared from futureStub!
                //ListenableFuture<OrderOuterClass.OrderEventResponse> future = futureStub.createOrder(request);
                asyncStub.createOrder(request, new OrderEventResponseProcessor());

            } catch (StatusRuntimeException e) {
                logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
                return;
            }
        }

        return;
    }
}
