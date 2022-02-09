/*
 * Copyright 2018-2022 Hazelcast, Inc
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
 *  limitations under the License.
 */
package org.hazelcast.msfdemo.acctsvc.business;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.flakeidgen.FlakeIdGenerator;
import com.hazelcast.jet.datamodel.Tuple2;
import com.hazelcast.jet.pipeline.JournalInitialPosition;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.ServiceFactories;
import com.hazelcast.jet.pipeline.ServiceFactory;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.Sources;
import com.hazelcast.jet.pipeline.StreamStage;
import com.hazelcast.map.IMap;
import org.hazelcast.msf.controller.MSFController;
import org.hazelcast.msf.messaging.APIResponse;
import org.hazelcast.msfdemo.acctsvc.domain.Account;
import org.hazelcast.msfdemo.acctsvc.events.AccountEventTypes;
import org.hazelcast.msfdemo.acctsvc.events.OpenAccountEvent;
import org.hazelcast.msfdemo.acctsvc.eventstore.AccountEventStore;
import org.hazelcast.msfdemo.acctsvc.service.AccountService;

import java.io.File;
import java.net.URL;
import java.util.AbstractMap;

import static com.hazelcast.jet.datamodel.Tuple2.tuple2;
import static org.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.OpenAccountRequest;

public class OpenAccountPipeline implements Runnable {

    private static AccountService service;

    public OpenAccountPipeline(AccountService service) {
        OpenAccountPipeline.service = service;
        if (service == null)
            throw new IllegalArgumentException("Service cannot be null");
    }

    @Override
    public void run() {
        try {
            MSFController controller = MSFController.getOrCreateInstance(service.isEmbedded(), service.getClientConfig());
            File fw = new File("/ext/framework-1.0-SNAPSHOT.jar");
            URL framework = fw.toURI().toURL();
            File grpc = new File("/ext/account-proto-1.0-SNAPSHOT.jar");
            URL grpcdefs = grpc.toURI().toURL();
            File svc = new File("/application.jar");
            URL service = svc.toURI().toURL();
            //System.out.println(">>> Found files? " + fw.exists() + " " + grpc.exists() + " " + svc.exists());
            URL[] jobJars = new URL[] { framework, grpcdefs, service };
            Class[] jobClasses = new Class[] {}; // {AccountOuterClass.class };
            System.out.println("OpenAccountPipeline.run() invoked, submitting job");
            controller.startJob("AccountService", "AccountService.OpenAccount", createPipeline());
            //controller.startJob("AccountService", "AccountService.OpenAccount", createPipeline(), jobJars, jobClasses);

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

        ServiceFactory<?, FlakeIdGenerator> sequenceGeneratorServiceFactory =
                ServiceFactories.sharedService(
                        (ctx) -> {
                            // Stuffing HZ into the event object while we have it, as in
                            // the mapping stage we can't retrieve it easily
                            HazelcastInstance hz = ctx.hazelcastInstance();
                            OpenAccountEvent.setHazelcastInstance(hz);
                            return hz.getFlakeIdGenerator("accountNumber");
                        }
                );

        StreamStage<Tuple2<Long,OpenAccountEvent>> tupleStream = p.readFrom(Sources.mapJournal(requestMap,
                JournalInitialPosition.START_FROM_OLDEST))
                .withIngestionTimestamps()
                .setName("Read from " + requestMapName)

                // Create AccountEvent object
                .mapUsingService(sequenceGeneratorServiceFactory, (seqGen, entry) -> {
                    Long uniqueRequestID = entry.getKey();
                    OpenAccountRequest request = entry.getValue();
                    long acctNumber = seqGen.newId();
                    OpenAccountEvent event = new OpenAccountEvent(
                            ""+acctNumber, request.getAccountName(), request.getInitialBalance());
                    Tuple2<Long,OpenAccountEvent> item = tuple2(uniqueRequestID, event);
                    return item;
                })
                .setName("Create AccountEvent.OPEN");

        // Persist to Event Store
        ServiceFactory<?, AccountEventStore> eventStoreServiceFactory =
                ServiceFactories.sharedService(
                        (ctx) -> new AccountEventStore(ctx.hazelcastInstance())
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

        .map( tuple -> {
            Long uniqueID = tuple.f0();
            Account account = tuple.f1();
            APIResponse<String> response = new APIResponse<String>(uniqueID,
                   account.getAcctNumber());
            return new AbstractMap.SimpleEntry<Long,APIResponse<String>>(uniqueID, response);
        }).setName("Build client APIResponse")
                .writeTo(Sinks.map(responseMap))
                .setName("Send response to client");

        return p;
    }
}
