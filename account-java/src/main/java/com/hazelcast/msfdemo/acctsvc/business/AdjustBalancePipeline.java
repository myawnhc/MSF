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

package com.hazelcast.msfdemo.acctsvc.business;

import com.hazelcast.jet.datamodel.Tuple2;
import com.hazelcast.jet.pipeline.JournalInitialPosition;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.ServiceFactories;
import com.hazelcast.jet.pipeline.ServiceFactory;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.Sources;
import com.hazelcast.jet.pipeline.StreamStage;
import com.hazelcast.jet.pipeline.WindowDefinition;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.IMap;
import com.hazelcast.msf.controller.MSFController;
import com.hazelcast.msf.messaging.APIResponse;
import com.hazelcast.msfdemo.acctsvc.domain.Account;
import com.hazelcast.msfdemo.acctsvc.events.AccountEventTypes;
import com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceRequest;
import com.hazelcast.msfdemo.acctsvc.events.AdjustBalanceEvent;
import com.hazelcast.msfdemo.acctsvc.eventstore.AccountEventStore;
import com.hazelcast.msfdemo.acctsvc.service.AccountService;

import java.io.File;
import java.util.AbstractMap;

import static com.hazelcast.jet.datamodel.Tuple2.tuple2;

public class AdjustBalancePipeline implements Runnable {

    private static AccountService service;

    public AdjustBalancePipeline(AccountService service) {
        this.service = service;
    }

    @Override
    public void run() {
        try {
            MSFController controller = MSFController.getInstance();
            File f = new File("./account/target/AccountService-1.0-SNAPSHOT.jar");
            //System.out.println("AdjustBalancePipeline Found service: " + f.exists());
            System.out.println("AdjustBalancePipeline.run() invoked, submitting job");
            controller.startJob("AccountService", "AccountService.AdjustBalance", f, createPipeline());
        } catch (Exception e) { // Happens if our pipeline is not valid
            e.printStackTrace();
        }
    }

    private static Pipeline createPipeline() {
        Pipeline p = Pipeline.create();
        String requestMapName = AccountEventTypes.ADJUST.getQualifiedName();
        IMap<Long, AdjustBalanceRequest> requestMap = MSFController.getInstance().getMap(requestMapName);
        String responseMapName = requestMapName + ".Results";
        IMap<Long, APIResponse<?>> responseMap = MSFController.getInstance().getMap(responseMapName);
        WindowDefinition oneSecond = WindowDefinition.sliding(1000, 1000);
        WindowDefinition tenSeconds = WindowDefinition.sliding(10000, 10000);

        // Kind of a pain that we have to propagate the request ID throughout the entire
        // pipeline but don't want to pollute domain objects with it.
        StreamStage<Tuple2<Long, AdjustBalanceEvent>> tupleStream = p.readFrom(Sources.mapJournal(requestMap,
                JournalInitialPosition.START_FROM_OLDEST))
                .withIngestionTimestamps()
                .setName("Read from " + requestMapName)

                // Not needed: filter - here a nop.
                // Not needed: transform - handle versioning, nop for now
                // Not needed: enrich - nothing to do for an ADJUST

                // Create AccountEvent object
                .map(entry -> {
                    //System.out.println("Creating AccountEvent, returning Tuple2");
                    Long uniqueRequestID = (Long) entry.getKey();
                    AdjustBalanceRequest request = entry.getValue();
                    AdjustBalanceEvent event = new AdjustBalanceEvent(
                            request.getAccountNumber(), request.getAmount());
                    Tuple2<Long,AdjustBalanceEvent> item = tuple2(uniqueRequestID, event);
                    return item;
                })
                .setName("Create AccountEvent.ADJUST");

        // Peek in on progress -- will probably remove this soon
//        tupleStream.window(oneSecond)
//                .aggregate(AggregateOperations.counting())
//                .writeTo(Sinks.logger(count -> "AccountEvent.ADJUST count " + count));

//        tupleStream.rollingAggregate(AggregateOperations.summingLong(tuple->tuple.f1().getAmount()))
//                //.window(tenSeconds)
//                .writeTo(Sinks.logger(runningTotal -> "Total adjustments so far " + runningTotal));
//
//        // Show the amount during the window
//        tupleStream.window(tenSeconds)
//                .aggregate(AggregateOperations.summingLong(tuple -> tuple.f1().getAmount()))
//                .writeTo(Sinks.logger(windowTotal -> "Total adjustments during window " + windowTotal));

        ServiceFactory<?,IMap<String,Account>> materializedViewServiceFactory = ServiceFactories.iMapService(service.getView().getName());

        ServiceFactory<?, AccountEventStore> eventStoreServiceFactory =
               ServiceFactories.sharedService(
                        (ctx) -> AccountEventStore.getInstance()
                );

        tupleStream.mapUsingService(eventStoreServiceFactory, (eventStore, tuple) -> {
            eventStore.append(tuple.f1());
            return tuple; // pass thru unchanged
        }).setName("Persist AdjustBalanceEvent to event store")

        // Build Materialized View and Publish it
        .mapUsingService(materializedViewServiceFactory, (viewMap, tuple) -> {
            AdjustBalanceEvent adjustEvent = tuple.f1();
            // How not to do this (race condition)
            //Account mview = viewMap.get(adjustEvent.getAccountNumber());
            //mview.setBalance(mview.getBalance() + adjustEvent.getAmount());
            //viewMap.put(adjustEvent.getAccountNumber(), mview);
            assert adjustEvent != null;
            Account accountView = viewMap.executeOnKey(adjustEvent.getAccountNumber(),
                    (EntryProcessor<String, Account, Account>) accountEntry -> {
                Account accountView1 = accountEntry.getValue();
                int newBalance = accountView1.getBalance() + adjustEvent.getAmount();
                accountView1.setBalance(newBalance);
                accountEntry.setValue(accountView1);
                return accountView1;
            });
            return tuple2(tuple.f0(), accountView);
        }).setName("Update Account Materialized View")

        // Build API response and publish it
        .map( tuple -> {
            Long uniqueID = tuple.f0();
            Account view = tuple.f1();
            APIResponse<Integer> response = new APIResponse<>(uniqueID,
                    view.getBalance());
            //System.out.println("Building and returning API response");
            return new AbstractMap.SimpleEntry<Long,APIResponse<Integer>>(uniqueID, response);
        }).setName("Respond to client")
                .writeTo(Sinks.map(responseMap));

        return p;
    }
}