package com.hazelcast.msfdemo.ordersvc.business;

import com.hazelcast.map.IMap;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.msf.controller.MSFController;
import com.hazelcast.msf.messaging.APIResponse;
import com.hazelcast.msfdemo.ordersvc.events.OrderEvent;
import com.hazelcast.msfdemo.ordersvc.events.OrderEventTypes;
import com.hazelcast.msfdemo.ordersvc.events.OrderGrpc;
import com.hazelcast.msfdemo.ordersvc.eventstore.OrderEventStore;
import com.hazelcast.query.Predicates;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreateOrderRequest;
import static com.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderEventResponse;

/** Server-side implementation of the OrderService API
 *  Takes requests and puts them to API-specific IMaps that trigger Jet pipelines
 *  Looks for result in corresponding result map to return to client
 */
public class OrderAPIImpl extends OrderGrpc.OrderImplBase {

    final MSFController controller = MSFController.getInstance();

    // CREATE
    final String createRequestMapName = OrderEventTypes.CREATE.getQualifiedName();
    final IMap<Long, CreateOrderRequest> orderPipelineInput = controller.getMap(createRequestMapName);
    final String createResponseMapName = createRequestMapName + ".Results";
    final IMap<Long, APIResponse<String>> orderPipelineOutput = controller.getMap(createResponseMapName);

    final private Map<Long, UUID> listenersByRequestID = new HashMap<>();


    @Override
    public void createOrder(CreateOrderRequest request, StreamObserver<OrderEventResponse> responseObserver) {
        // Unique ID used to pair up requests with responses
        long uniqueID = controller.getUniqueMessageID();

        // Get listener to result map armed before we trigger the pipeline
        UUID listenerID = orderPipelineOutput.addEntryListener((EntryAddedListener<Long, APIResponse<OrderEvent>>) entryEvent -> {
            //System.out.println("OPEN completion listener fired for ID " + uniqueID);
            APIResponse<OrderEvent> apiResponse = entryEvent.getValue();
            if (apiResponse.getStatus() == APIResponse.Status.SUCCESS) {
                OrderEvent event = apiResponse.getResultValue();
                OrderEventResponse grpcResponse =
                        OrderEventResponse.newBuilder()
                                .setOrderNumber(event.getOrderNumber())
                                .setAccountNumber(event.getAccountNumber())
                                .setItemNumber(event.getItemNumber())
                                .setLocation(event.getLocation())
                                .setQuantity(event.getQuantity())
                                .setEventName(event.getEventName())
                                .build();
                System.out.println("OrderAPIImpl sending first response from APIResponse");
                responseObserver.onNext(grpcResponse);
                // Handle additional events for the order
                new EventHandler(event.getOrderNumber(), responseObserver);
            } else {
                responseObserver.onError(apiResponse.getError());
            }
            // TODO: may send a stream of responses, in which case we do not want to
            // mark completed until the order actually ships ...

            //responseObserver.onCompleted();
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

    @Deprecated // eventHandler instantiated directly in above method
    private void processOrder(String orderNumber, StreamObserver<OrderEventResponse> responseObserver) {
        //OrderEventStore eventStore = OrderEventStore.getInstance();
        System.out.println("Registered to monitor event store for subsequent events");
        // TODO: need to unregister once temrinal event is received
        new EventHandler(orderNumber, responseObserver);
    }

    public static class EventHandler {
        private StreamObserver<OrderEventResponse> responseObserver;
        private OrderEventStore eventStore;
        private UUID listenerID;

        public EventHandler(String orderNumber, StreamObserver<OrderEventResponse> observer) {
            this.responseObserver = observer;
            this.eventStore = OrderEventStore.getInstance();
            this.listenerID = eventStore.registerEventHandler(orderNumber, this);
            System.out.println("Registered handler for subsequent events on order " + orderNumber);
        }

        // Needs responseStream in order to respond!
        public void handleEvent(OrderEvent event) {
            System.out.println("New event : " + event);
            OrderEventResponse grpcResponse =
                    OrderEventResponse.newBuilder()
                            .setOrderNumber(event.getOrderNumber())
                            .setAccountNumber(event.getAccountNumber())
                            .setItemNumber(event.getItemNumber())
                            .setLocation(event.getLocation())
                            .setQuantity(event.getQuantity())
                            .setExtendedPrice(event.getExtendedPrice())
                            .setEventName(event.getEventName())
                            .build();
            responseObserver.onNext(grpcResponse);
            if (event.isTerminal()) {
                System.out.println("Terminal event, calling onCompleted");
                responseObserver.onCompleted();
                eventStore.removeEventListener(listenerID);
            }
        }
    }
}