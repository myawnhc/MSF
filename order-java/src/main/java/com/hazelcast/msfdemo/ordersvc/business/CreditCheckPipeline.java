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

package com.hazelcast.msfdemo.ordersvc.business;

import com.hazelcast.cp.IAtomicLong;
import com.hazelcast.jet.datamodel.Tuple2;
import com.hazelcast.jet.grpc.GrpcService;
import com.hazelcast.jet.pipeline.JournalInitialPosition;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.ServiceFactories;
import com.hazelcast.jet.pipeline.ServiceFactory;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.Sources;
import com.hazelcast.jet.pipeline.StreamStage;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.IMap;
import com.hazelcast.msf.configuration.ServiceConfig;
import com.hazelcast.msf.controller.MSFController;
import com.hazelcast.msf.messaging.StreamObserverToIMapAdapter;
import com.hazelcast.msfdemo.acctsvc.events.AccountGrpc;
import com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass;
import com.hazelcast.msfdemo.ordersvc.domain.Order;
import com.hazelcast.msfdemo.ordersvc.domain.WaitingOn;
import com.hazelcast.msfdemo.ordersvc.events.AccountInventoryCombo;
import com.hazelcast.msfdemo.ordersvc.events.CreditCheckEvent;
import com.hazelcast.msfdemo.ordersvc.events.PriceLookupEvent;
import com.hazelcast.msfdemo.ordersvc.eventstore.OrderEventStore;
import com.hazelcast.msfdemo.ordersvc.service.OrderService;
import io.grpc.ManagedChannelBuilder;

import java.io.File;
import java.util.EnumSet;

import static com.hazelcast.jet.datamodel.Tuple2.tuple2;
import static com.hazelcast.jet.grpc.GrpcServices.unaryService;
import static com.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderPriced;

public class CreditCheckPipeline implements Runnable {

    private static OrderService orderService;
    private static String accountServiceHost;
    private static int accountServicePort;
    private static IMap<Long, OrderPriced> orderPricedEvents;

    private static final String PENDING_MAP_NAME = "pendingValidation";
    private static final String COMPLETED_MAP_NAME = "JRN.completedValidation";

    public CreditCheckPipeline(OrderService service) {
        CreditCheckPipeline.orderService = service;
    }

    @Override
    public void run() {
        try {
            MSFController controller = MSFController.getInstance();

            // Foreign service configuration
            ServiceConfig.ServiceProperties props = ServiceConfig.get("account-service");
            accountServiceHost = props.getGrpcHostname();
            accountServicePort = props.getGrpcPort();

            // We are invoked following PriceLookup, so subscribe to those notifications
            String mapName = "JRN.CCP." + OrderPriced.getDescriptor().getFullName();
            orderPricedEvents = controller.getMap(mapName);
            IAtomicLong sequence = controller.getSequenceGenerator(mapName);
            PriceLookupEvent.subscribe(new StreamObserverToIMapAdapter<>(orderPricedEvents, sequence));

            // Build pipeline and submit job
            File f = new File("./order/target/OrderService-1.0-SNAPSHOT.jar");
            System.out.println("CreditCheckPipeline.run() invoked, submitting job");
            controller.startJob("OrderService", "OrderService.CreditCheck", f, createPipeline());

        } catch (Exception e) {
            // Happens if pipeline is invalid
            e.printStackTrace();
        }
    }

    public static Pipeline createPipeline() {

        // Remote gRPC service (AccountService RequestAuth )
        ServiceFactory<?, ? extends GrpcService<AccountOuterClass.AuthorizationRequest, AccountOuterClass.AuthorizationResponse>>
                accountService = unaryService(
                () -> ManagedChannelBuilder.forAddress(accountServiceHost, accountServicePort) .usePlaintext(),
                channel -> AccountGrpc.newStub(channel)::requestAuth);

        // EventStore as a service
        ServiceFactory<?, OrderEventStore> eventStoreServiceFactory =
                ServiceFactories.sharedService((ctx) -> OrderEventStore.getInstance());

        // IMap/Materialized View as a service
        ServiceFactory<?, IMap<String,Order>> materializedViewServiceFactory =
                ServiceFactories.iMapService(orderService.getView().getName());

        // Maps for MapUsingIMap calls (not serializable, cannot reference outside those calls)
        IMap<String, Order> orderMap = orderService.getView();

        ServiceFactory<?, IMap<String, AccountInventoryCombo>> pendingMapService =
                ServiceFactories.iMapService(PENDING_MAP_NAME);

//        ServiceFactory<?, IMap<String, AccountInventoryCombo>> completedMapService =
//                ServiceFactories.iMapService(COMPLETED_MAP_NAME);

        Pipeline p = Pipeline.create();
        StreamStage<OrderPriced> pricingStream = p.readFrom(Sources.mapJournal(orderPricedEvents,
                JournalInitialPosition.START_FROM_OLDEST))
                .withIngestionTimestamps()
                .map(entry -> entry.getValue())
                .setName("Read from " + orderPricedEvents.getName());

        // OrderPriced event doesn't contain account number, so must enrich stream
        StreamStage<Tuple2<OrderPriced,Order>> enrichedItems = pricingStream.mapUsingIMap(
                orderMap,
                /* lookupKeyFn */ op->op.getOrderNumber(),
                /* mapFN */ (orderPriced, order) -> tuple2(orderPriced, order));

        StreamStage<CreditCheckEvent> creditCheckEvents = enrichedItems.mapUsingServiceAsync(accountService, (service, tuple) -> {
            String account = tuple.f1().getAcctNumber();
            int amount = tuple.f1().getExtendedPrice() * tuple.f1().getQuantity();
            AccountOuterClass.AuthorizationRequest request = AccountOuterClass.AuthorizationRequest.newBuilder()
                    .setAccountNumber(account)
                    .setRequestedAmount(amount)
                    .build();
            return service.call(request)
                    .thenApply(response -> {
                        CreditCheckEvent ccevent = new CreditCheckEvent(tuple.f1().getOrderNumber());
                        ccevent.setAccountNumber(tuple.f1().getAcctNumber());
                        ccevent.setAmountRequested(amount);
                        ccevent.setSufficient(response.getApproved());
                        return ccevent;
                    });
        });

        // Persist to event store
        creditCheckEvents.mapUsingService(eventStoreServiceFactory, (store, ccevent) -> {
            store.append(ccevent);
            return ccevent;
        }).setName("Persist CreditCheckEvent to event store")
        // Update materialized view
                .mapUsingService(materializedViewServiceFactory, (viewMap, ccevent) -> {
                    Order order = viewMap.executeOnKey(ccevent.getOrderNumber(), (EntryProcessor<String, Order, Order>) orderEntry -> {
                        Order orderView = orderEntry.getValue();
                        EnumSet<WaitingOn> waits = orderView.getWaitingOn();
                        waits.remove(WaitingOn.CREDIT_CHECK);
                        System.out.println("* After removing C_C: " + waits.toString());
                        if (waits.isEmpty()) {
                            waits.add(WaitingOn.CHARGE_ACCOUNT);
                            waits.add(WaitingOn.PULL_INVENTORY);
                            orderEntry.setValue(orderView);
                            System.out.println("After removing CC wait, empty, reset and pass on");
                            return orderView;
                        } else {
                            orderEntry.setValue(orderView);
                            System.out.println("After removing CC wait, non-empty so filtering");
                            return null;
                        }
                    });
                    return order == null ? null : ccevent;
                })
                .setName("Update order Materialized View")
                .mapUsingService(pendingMapService, (map, ccevent) -> {

                    String orderNumber = ccevent.getOrderNumber();
                    AccountInventoryCombo combo = map.get(orderNumber);
                    System.out.println("Combo: " + combo);
                    if (combo != null) {
                        // validate
                        if (!combo.hasInventoryFields()) {
                            System.out.println("WARNING: pending entry has no inventory data");
                        }
                        combo.setAccountFields(ccevent);
                        map.remove(combo);
                        //completedMap.set(orderNumber, combo);
                        System.out.println("Combo completed with acct fields " + combo);
                        return combo;
                    } else {
                        combo = new AccountInventoryCombo();
                        combo.setAccountFields(ccevent);
                        map.set(combo.getOrderNumber(), combo);
                        System.out.println("Combo created with acct fields");
                        return null;
                    }
                })
                .setName("Merge account and inventory results")
                .setName("Merge inventory and account results into combo item")
                .writeTo(Sinks.map(COMPLETED_MAP_NAME,
                        /* toKeyFn*/ combo -> combo.getOrderNumber(),
                        /* toValueFn */ combo -> combo))
                .setName("Sink inv-acct combo item into map");
        return p;
    }
}
