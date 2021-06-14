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
import com.hazelcast.msfdemo.acctsvc.domain.Account;
import com.hazelcast.msfdemo.acctsvc.events.AccountEventTypes;
import com.hazelcast.msfdemo.acctsvc.events.OpenAccountEvent;
import com.hazelcast.msfdemo.acctsvc.eventstore.AccountEventStore;
import com.hazelcast.msfdemo.acctsvc.service.AccountService;

import java.io.File;
import java.util.AbstractMap;

import static com.hazelcast.jet.datamodel.Tuple2.tuple2;
import static com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.OpenAccountRequest;

public class OpenAccountPipeline implements Runnable {

    private static AccountService service;

    public OpenAccountPipeline(AccountService service) {
        this.service = service;
    }

    @Override
    public void run() {
        try {
            MSFController controller = MSFController.getInstance();
            File f = new File("./account/target/AccountService-1.0-SNAPSHOT.jar");
            //System.out.println("OpenAccountPipeline Found service: " + f.exists());
            System.out.println("OpenAccountPipeline.run() invoked, submitting job");
            controller.startJob("AccountService", "AccountService.OpenAccount", f, createPipeline());
        } catch (Exception e) { // Happens if our pipeline is not valid
            e.printStackTrace();
        }
    }

    private static Pipeline createPipeline() {
        Pipeline p = Pipeline.create();
        String requestMapName = AccountEventTypes.OPEN.getQualifiedName();
        IMap<Long, OpenAccountRequest> requestMap = MSFController.getInstance().getMap(requestMapName);
        String responseMapName = requestMapName + ".Results";
        IMap<Long, APIResponse<?>> responseMap = MSFController.getInstance().getMap(responseMapName);
        WindowDefinition oneSecond = WindowDefinition.sliding(1000, 1000);
        // Kind of a pain that we have to propagate the request ID throughout the entire
        // pipeline but don't want to pollute domain objects with it.
        StreamStage<Tuple2<Long,OpenAccountEvent>> tupleStream = p.readFrom(Sources.mapJournal(requestMap,
                JournalInitialPosition.START_FROM_OLDEST))
                .withIngestionTimestamps()
                .setName("Read from " + requestMapName)
                //.map(entry -> (AccountOuterClass.OpenAccountRequest) entry.getValue());
                //.writeTo(Sinks.logger());
                // Not needed: filter - here a nop.
                // Not needed: transform - handle versioning, nop for now
                // Not needed: enrich - nothing to do for an OPEN
                // Create AccountEvent object
                .map(entry -> {
                    //System.out.println("Creating AccountEvent, returning Tuple2");
                    Long uniqueRequestID = (Long) entry.getKey();
                    OpenAccountRequest request = entry.getValue();
                    long acctNumber = MSFController.getInstance().getUniqueId("accountNumber");
                    OpenAccountEvent event = new OpenAccountEvent(
                            ""+acctNumber, request.getAccountName(), request.getInitialBalance());
                    Tuple2<Long,OpenAccountEvent> item = tuple2(uniqueRequestID, event);
                    return item;
                })
                .setName("Create AccountEvent.OPEN");

//        // Drop the UniqueID for most use cases
//        StreamStage<OpenAccountEvent> eventStream = tupleStream.map( tuple -> tuple.f1())
//                .setName("Simplify stream item (drop uniqueItemID)");

        // Peek in on progress
        tupleStream.window(oneSecond)
                .aggregate(AggregateOperations.counting())
                .setName("Count operations per second")
                .writeTo(Sinks.logger(count -> "AccountEvent.OPEN count " + count));

        // Persist to Event Store and Materialized View
        final IMap<String, Account> accountView = service.getView();
        ServiceFactory<?, AccountEventStore> eventStoreServiceFactory =
                ServiceFactories.sharedService(
                        (ctx) -> AccountEventStore.getInstance()
                );

        ServiceFactory<?,IMap<String,Account>> materializedViewServiceFactory = ServiceFactories.iMapService(service.getView().getName());


        tupleStream.mapUsingService( eventStoreServiceFactory, (eventStore, tuple) -> {
                    eventStore.append(tuple.f1());
                    return tuple;
                }).setName("Persist OpenAccountEvent to event store")

        // Create Materialized View object and publish it
        .mapUsingService(materializedViewServiceFactory, (viewMap, tuple)-> {
            //System.out.println("Creating Account (Materialized View) object");
            OpenAccountEvent openEvent = tuple.f1();
            Account a = new Account();
            a.setName(openEvent.getAccountName());
            a.setAcctNumber(openEvent.getAccountNumber());
            a.setBalance(openEvent.getAmount());
            viewMap.put(openEvent.getAccountNumber(), a);
            return tuple2(tuple.f0(), a);
        }).setName("Create and publish Account Materialized View")
//                //.writeTo(Sinks.logger());
//                .writeTo(Sinks.map(accountView))
//                .setName("Insert into Materialized View map"); // Will be map w/Merge or Update for subsequent txns

        .map( tuple -> {
            Long uniqueID = tuple.f0();
            Account account = tuple.f1();
            APIResponse<String> response = new APIResponse<String>(uniqueID,
                   account.getAcctNumber());
            //System.out.println("Building and returning API response");
            return new AbstractMap.SimpleEntry<Long,APIResponse<String>>(uniqueID, response);
        }).setName("Build client APIResponse")
                .writeTo(Sinks.map(responseMap))
                .setName("Send response to client");

        return p;
    }
}
