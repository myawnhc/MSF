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
import com.hazelcast.jet.aggregate.AggregateOperations;
import com.hazelcast.jet.datamodel.Tuple2;
import com.hazelcast.jet.grpc.GrpcService;
import com.hazelcast.jet.pipeline.JournalInitialPosition;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.ServiceFactories;
import com.hazelcast.jet.pipeline.ServiceFactory;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.Sources;
import com.hazelcast.jet.pipeline.StageWithKeyAndWindow;
import com.hazelcast.jet.pipeline.StreamStage;
import com.hazelcast.jet.pipeline.StreamStageWithKey;
import com.hazelcast.jet.pipeline.WindowDefinition;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.IMap;
import com.hazelcast.msf.configuration.ServiceConfig;
import com.hazelcast.msf.controller.MSFController;
import com.hazelcast.msf.messaging.StreamObserverToIMapAdapter;
import com.hazelcast.msfdemo.acctsvc.events.AccountGrpc;
import com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass;
import com.hazelcast.msfdemo.ordersvc.domain.Order;
import com.hazelcast.msfdemo.ordersvc.domain.WaitingOn;
import com.hazelcast.msfdemo.ordersvc.events.ChargeAccountEvent;
import com.hazelcast.msfdemo.ordersvc.events.CreditCheckEvent;
import com.hazelcast.msfdemo.ordersvc.events.InventoryReserveEvent;
import com.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreditChecked;
import com.hazelcast.msfdemo.ordersvc.eventstore.OrderEventStore;
import com.hazelcast.msfdemo.ordersvc.service.OrderService;
import io.grpc.ManagedChannelBuilder;

import java.io.File;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hazelcast.jet.grpc.GrpcServices.unaryService;
import static com.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.InventoryReserved;

public class CollectPaymentPipeline implements Runnable {

    private static OrderService orderService;
    private static String accountServiceHost;
    private static int accountServicePort;
    private static IMap<Long, CreditChecked> creditCheckedEvents;
    private static IMap<Long, InventoryReserved> inventoryReservedEvents;

    public CollectPaymentPipeline(OrderService service) {
        CollectPaymentPipeline.orderService = service;
    }
    @Override
    public void run() {
        try {
            MSFController controller = MSFController.getInstance();

            // Foreign service configuration
            ServiceConfig.ServiceProperties props = ServiceConfig.get("account-service");
            accountServiceHost = props.getGrpcHostname();
            accountServicePort = props.getGrpcPort();

            // We are invoked by either of two events
            // and will use co-group to combine them
            String ccmap = "JRN.CPP." + CreditChecked.getDescriptor().getFullName();
            creditCheckedEvents = controller.getMap(ccmap);
            String irmap = "JRN.CPP." + InventoryReserved.getDescriptor().getFullName();
            inventoryReservedEvents = controller.getMap(irmap);
            IAtomicLong ccSequence = controller.getSequenceGenerator(ccmap);
            IAtomicLong irSequence = controller.getSequenceGenerator(irmap);
            CreditCheckEvent.subscribe(new StreamObserverToIMapAdapter<>(creditCheckedEvents, ccSequence));
            InventoryReserveEvent.subscribe(new StreamObserverToIMapAdapter<>(inventoryReservedEvents, irSequence));

            // Build pipeline and submit job
            File f = new File("./order/target/OrderService-1.0-SNAPSHOT.jar");
            System.out.println("CollectPaymentPipeline.run() invoked, submitting job");
            controller.startJob("OrderService", "OrderService.CollectPayment", f, createPipeline());

        } catch (Exception e) { // Happens if pipeline is not valid
            e.printStackTrace();
        }
    }

    private static Pipeline createPipeline() {

        // Remote gRPC service (AccountService RequestAuth )
        ServiceFactory<?, ? extends GrpcService<AccountOuterClass.AdjustBalanceRequest, AccountOuterClass.AdjustBalanceResponse>>
                accountService = unaryService(
                () -> ManagedChannelBuilder.forAddress(accountServiceHost, accountServicePort) .usePlaintext(),
                channel -> AccountGrpc.newStub(channel)::payment);


        // EventStore as a service
        ServiceFactory<?, OrderEventStore> eventStoreServiceFactory =
                ServiceFactories.sharedService((ctx) -> OrderEventStore.getInstance());

        // IMap/Materialized View as a service
        ServiceFactory<?, IMap<String,Order>> materializedViewServiceFatory =
                ServiceFactories.iMapService(orderService.getView().getName());

        // Seems like this is what co-group should be managing for us
        Map<String, CreditChecked> unmatchedCC = new HashMap<>();
        Map<String, InventoryReserved> unmatchedIR = new HashMap<>();

        // Testing co-group as way to join results from different pipelies
        Pipeline p = Pipeline.create();

        StreamStageWithKey<CreditChecked, String> ccevents = p.readFrom(Sources.mapJournal(
                creditCheckedEvents,
                JournalInitialPosition.START_FROM_OLDEST))
                .withIngestionTimestamps()
                .setName("Read from " + creditCheckedEvents.getName())
                .map(mapEntry -> mapEntry.getValue())
                .groupingKey(creditChecked -> creditChecked.getOrderNumber());

        StreamStageWithKey<InventoryReserved, String> irevents = p.readFrom(Sources.mapJournal(
                        inventoryReservedEvents,
                        JournalInitialPosition.START_FROM_OLDEST))
                .withIngestionTimestamps()
                .setName("Read from " + inventoryReservedEvents.getName())
                .map(mapEntry -> mapEntry.getValue())
                .groupingKey(inventoryReserved -> inventoryReserved.getOrderNumber());

       // Each stage has either a CC or an IR - none has both!
        // By doing sliding/overlapping, we're getting each event multiple times, and matching logic below doesn't
        // fully account for this resulting in duplicate payments being recorded!
       StageWithKeyAndWindow<CreditChecked, String> windowStage = ccevents.window(WindowDefinition.sliding(20, 1));

        // We really don't need to pass the InventoryResult out of this stage, we just
        // need to make sure we have one before continuing. Rather than re-code will
        // drop it from the next stage.
       StreamStage<Tuple2<CreditChecked,InventoryReserved>> pairedNotifications =
                windowStage.aggregate2(AggregateOperations.toList(), irevents, AggregateOperations.toList())
                .map(keyedWindowResult -> {
                    Tuple2<List<CreditChecked>,List<InventoryReserved>> tuple = keyedWindowResult.getValue();
                    List<CreditChecked> cc = tuple.f0();
                    List<InventoryReserved> ir = tuple.f1();
                    //System.out.println("CC list size: " + cc.size() + " IR list size: " + ir.size());
                    for (CreditChecked ccitem : cc) {
                        InventoryReserved iritem = unmatchedIR.remove(ccitem.getOrderNumber());
                        if (iritem != null) {
                            System.out.println("Made an ir match, unmatched IR size now " + unmatchedIR.size());
                            return Tuple2.tuple2(ccitem, iritem);
                        }
                        if (! unmatchedCC.containsKey(ccitem.getOrderNumber())) {
                            unmatchedCC.put(ccitem.getOrderNumber(), ccitem);
                            System.out.println("Added unmatched cc " + ccitem.getOrderNumber() + " # unmatched = " + unmatchedCC.size());

                        }
                    }
                    for (InventoryReserved iritem : ir) {
                        CreditChecked ccitem = unmatchedCC.remove(iritem.getOrderNumber());
                        if (ccitem != null) {
                            System.out.println("Made a cc match, unmatched CC size now " + unmatchedCC.size());
                            return Tuple2.tuple2(ccitem, iritem);
                        }
                        if (! unmatchedIR.containsKey(iritem.getOrderNumber())) {
                            unmatchedIR.put(iritem.getOrderNumber(), iritem);
                            System.out.println("Added unmatched ir " + iritem.getOrderNumber() + " # unmatched = " + unmatchedIR.size());
                        }
                    }
                    return null; // only non-matches get this far
                });

        // Enrichment stage not needed, CreditChecked has all the data we need
        // TODO: invoke the Account services Payment service
        StreamStage<ChargeAccountEvent> paymentEvents = pairedNotifications.mapUsingServiceAsync(accountService, (service, tuple) -> {
            String account = tuple.f0().getAccountNumber();
            int amount = tuple.f0().getAmountRequested();
            AccountOuterClass.AdjustBalanceRequest request = AccountOuterClass.AdjustBalanceRequest.newBuilder()
                    .setAccountNumber(account)
                    .setAmount(amount)
                    .build();
            return service.call(request)
                    .thenApply(response -> {
                        ChargeAccountEvent payment = new ChargeAccountEvent(tuple.f0().getOrderNumber());
                        payment.setAccountNumber(account);
                        payment.setAmountRequested(amount);
                        return payment;
                    });
        });

        // Write ChargeAccountEvent to Event Store
        paymentEvents.mapUsingService(eventStoreServiceFactory, (store, payment) -> {
                    store.append(payment);
                    return payment;
                }).setName("Persist ChargeAccountEvent to event store")

        // Update Materialized View including wait flags
        .mapUsingService(materializedViewServiceFatory, (viewMap, ccevent) -> {
            viewMap.executeOnKey(ccevent.getOrderNumber(), (EntryProcessor<String, Order, Order>) orderEntry -> {
                Order orderView = orderEntry.getValue();
                EnumSet<WaitingOn> waits = orderView.getWaitingOn();
                waits.remove(WaitingOn.CHARGE_ACCOUNT);
                if (waits.isEmpty()) {
                    waits.add(WaitingOn.SHIP);
                }
                orderEntry.setValue(orderView);
                return orderView;
            });
            return ccevent;
        }).setName("Update order Materialized View")
                .writeTo(Sinks.noop());
        return p;
    }
}
