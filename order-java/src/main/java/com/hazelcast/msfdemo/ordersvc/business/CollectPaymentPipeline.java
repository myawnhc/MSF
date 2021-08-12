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
import com.hazelcast.msfdemo.acctsvc.events.AccountGrpc;
import com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass;
import com.hazelcast.msfdemo.ordersvc.domain.Order;
import com.hazelcast.msfdemo.ordersvc.domain.WaitingOn;
import com.hazelcast.msfdemo.ordersvc.events.AccountInventoryCombo;
import com.hazelcast.msfdemo.ordersvc.events.ChargeAccountEvent;
import com.hazelcast.msfdemo.ordersvc.eventstore.OrderEventStore;
import com.hazelcast.msfdemo.ordersvc.service.OrderService;
import io.grpc.ManagedChannelBuilder;

import java.io.File;
import java.util.EnumSet;

import static com.hazelcast.jet.grpc.GrpcServices.unaryService;

public class CollectPaymentPipeline implements Runnable {

    private static OrderService orderService;
    private static String accountServiceHost;
    private static int accountServicePort;
    private static IMap<String, AccountInventoryCombo> acctInvCombos;

    private static final String PENDING_MAP_NAME = "pendingTransactions";
    private static final String COMPLETED_MAP_NAME = "JRN.completedTransactions";

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

            // We pull from map that has merged events
            String comboMap = "JRN.completedValidation";
            acctInvCombos = controller.getMap(comboMap);

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
        ServiceFactory<?, IMap<String,Order>> materializedViewServiceFactory =
                ServiceFactories.iMapService(orderService.getView().getName());

        // Pending map as a service
        ServiceFactory<?, IMap<String, AccountInventoryCombo>> pendingMapService =
                ServiceFactories.iMapService(PENDING_MAP_NAME);

        Pipeline p = Pipeline.create();

        StreamStage<AccountInventoryCombo> combos = p.readFrom(Sources.mapJournal(
                acctInvCombos,
                JournalInitialPosition.START_FROM_OLDEST))
                .withIngestionTimestamps()
                .setName("Read from " + acctInvCombos.getName())
                .map( entry -> entry.getValue());

        // Enrichment stage not needed, combo object
        StreamStage<ChargeAccountEvent> paymentEvents = combos.mapUsingServiceAsync(accountService, (service, combo) -> {
            String account = combo.getAccountNumber();
            int amount = combo.getAmountCharged();
            System.out.println("CollectPaymentPipeline - Sending Payment request to account service");
            AccountOuterClass.AdjustBalanceRequest request = AccountOuterClass.AdjustBalanceRequest.newBuilder()
                    .setAccountNumber(account)
                    .setAmount(amount)
                    .build();
            return service.call(request)
                    .thenApply(response -> {
                        ChargeAccountEvent payment = new ChargeAccountEvent(combo.getOrderNumber());
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
        .mapUsingService(materializedViewServiceFactory, (viewMap, ccevent) -> {
            viewMap.executeOnKey(ccevent.getOrderNumber(), (EntryProcessor<String, Order, Order>) orderEntry -> {
                Order orderView = orderEntry.getValue();
                EnumSet<WaitingOn> waits = orderView.getWaitingOn();
                waits.remove(WaitingOn.CHARGE_ACCOUNT);
                if (waits.isEmpty()) {
                    waits.add(WaitingOn.SHIP);
                }
                System.out.println("After removing CHARGE_ACCOUNT, waiting on: " + waits.toString());
                orderEntry.setValue(orderView);
                return orderView;
            });
            return ccevent;
        }).setName("Update order Materialized View")

                // Create or Update the Combo event (Inventory Pulled + Payment collected)
                .mapUsingService(pendingMapService, (map, cpevent) -> {
                    String orderNumber = cpevent.getOrderNumber();
                    AccountInventoryCombo combo = map.get(orderNumber);
                    if (combo != null) {
                        // validate
                        if (!combo.hasInventoryFields()) {
                            System.out.println("WARNING: pending combo entry has no inventory data");
                        }
                        combo.setAccountFields(cpevent);
                        map.remove(cpevent);
                        System.out.println("IRPipeline: CC+IR Combo completed with account fields " + combo);
                        return combo;
                    } else {
                        combo = new AccountInventoryCombo();
                        combo.setAccountFields(cpevent);
                        map.set(combo.getOrderNumber(), combo);
                        System.out.println("CPPipeline: CP+IP Combo created with account fields");
                        return null;
                    }
                })
                .setName("Merge inventory and account results into combo item")

                // If CP+IP both present, sink into completed map to pass to next stages
                .writeTo(Sinks.map(COMPLETED_MAP_NAME,
                        /* toKeyFn*/ combo -> combo.getOrderNumber(),
                        /* toValueFn */ combo -> combo))
                .setName("Sink inv-acct combo item into map");
        return p;
    }
}
