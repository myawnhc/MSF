package com.hazelcast.msfdemo.ordersvc.business;

import com.hazelcast.jet.aggregate.AggregateOperations;
import com.hazelcast.jet.datamodel.Tuple2;
import com.hazelcast.jet.pipeline.JournalInitialPosition;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.ServiceFactories;
import com.hazelcast.jet.pipeline.ServiceFactory;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.Sources;
import com.hazelcast.jet.pipeline.StreamStage;
import com.hazelcast.jet.pipeline.WindowDefinition;
import com.hazelcast.map.IMap;
import com.hazelcast.msf.controller.MSFController;
import com.hazelcast.msf.messaging.APIResponse;
import com.hazelcast.msfdemo.ordersvc.domain.Order;
import com.hazelcast.msfdemo.ordersvc.events.CreateOrderEvent;
import com.hazelcast.msfdemo.ordersvc.events.OrderEvent;
import com.hazelcast.msfdemo.ordersvc.events.OrderEventTypes;
import com.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreateOrderRequest;
import com.hazelcast.msfdemo.ordersvc.eventstore.OrderEventStore;
import com.hazelcast.msfdemo.ordersvc.service.OrderService;

import java.io.File;
import java.util.AbstractMap;

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
            File f = new File("./order/target/OrderService-1.0-SNAPSHOT.jar");
            System.out.println("CreateOrderPipeline.run() invoked, submitting job");
            controller.startJob("OrderService", "OrderService.CreateOrder", f, createPipeline());
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
        WindowDefinition oneSecond = WindowDefinition.sliding(1000, 1000);
        // Kind of a pain that we have to propagate the request ID throughout the entire
        // pipeline but don't want to pollute domain objects with it.
        StreamStage<Tuple2<Long,CreateOrderEvent>> tupleStream = p.readFrom(Sources.mapJournal(requestMap,
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
        tupleStream.window(oneSecond)
                .aggregate(AggregateOperations.counting())
                .setName("Count operations per second")
                .writeTo(Sinks.logger(count -> "OrderEvent.CREATE count " + count));

        // Persist to Event Store and Materialized View
        ServiceFactory<?, OrderEventStore> eventStoreServiceFactory =
                ServiceFactories.sharedService(
                        (ctx) -> OrderEventStore.getInstance()
                );

        ServiceFactory<?,IMap<String,Order>> materializedViewServiceFactory = ServiceFactories.iMapService(service.getView().getName());


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
            o.setExtendedPrice(orderEvent.getExtendedPrice());
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
