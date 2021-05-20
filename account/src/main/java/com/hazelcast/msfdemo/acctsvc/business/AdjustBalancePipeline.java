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
import com.hazelcast.msfdemo.acctsvc.events.AccountEvent;
import com.hazelcast.msfdemo.acctsvc.events.AccountEventTypes;
import com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceRequest;
import com.hazelcast.msfdemo.acctsvc.events.AdjustBalanceEvent;
import com.hazelcast.msfdemo.acctsvc.service.AccountService;

import static com.hazelcast.jet.datamodel.Tuple2.tuple2;

import java.util.AbstractMap;

public class AdjustBalancePipeline implements Runnable {

    private static AccountService service;

    public AdjustBalancePipeline(AccountService service) {
        this.service = service;
    }

    @Override
    public void run() {
        try {
            MSFController controller = MSFController.getInstance();
            System.out.println("AdjustBalancePipeline.run() invoked, submitting job");
            controller.startJob("AccountService.AdjustBalance", createPipeline());
        } catch (Exception e) { // Happens if our pipeline is not valid
            e.printStackTrace();
        }
    }

    private static Pipeline createPipeline() {
        Pipeline p = Pipeline.create();
        String requestMapName = AccountEventTypes.ADJUST.getQualifiedName();
        IMap<Long, AdjustBalanceRequest> requestMap = MSFController.getInstance().getMap(requestMapName);
        String responseMapName = requestMapName + "Results";
        IMap<Long, APIResponse<?>> responseMap = MSFController.getInstance().getMap(responseMapName);
        WindowDefinition oneSecond = WindowDefinition.sliding(1000, 1000);
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

        // Drop the UniqueID for most use cases
        //StreamStage<AdjustBalanceEvent> eventStream = tupleStream.map( tuple -> tuple.f1());

        // Peek in on progress -- will probably remove this soon
        tupleStream.window(oneSecond)
                .aggregate(AggregateOperations.counting())
                .writeTo(Sinks.logger(count -> "AccountEvent.ADJUST count " + count));

        // Yet another rework ....
        // Persist to the EventStore
        ServiceFactory<?,IMap<Long,AccountEvent>> eventStoreServiceFactory = ServiceFactories.iMapService(service.getEventStore().getEventMap().getName());
        ServiceFactory<?,IMap<String,Account>> materializedViewServiceFactory = ServiceFactories.iMapService(service.getView().getName());
        tupleStream.mapUsingService(eventStoreServiceFactory, (eventMap, tuple) -> {
            AdjustBalanceEvent abe = tuple.f1();
            // append would do this for us but if we are inserting into the
            // event store manually, we have to do sequencing manually as well
            Long key = service.getEventStore().getNextSequence();
            abe.setSequence(key);
            eventMap.put(key, abe);
            return tuple; // pass thru unchanged

        }).setName("Persist AdjustBalanceEvent to event store")

        // Build Materialized View and Publish it
        .mapUsingService(materializedViewServiceFactory, (viewMap, tuple) -> {
            AdjustBalanceEvent adjustEvent = tuple.f1();
            Account mview = viewMap.get(adjustEvent.getAccountNumber());
            // TODO: handle invalid account number -> null mview 
            mview.setBalance(mview.getBalance() + adjustEvent.getAmount());
            viewMap.put(adjustEvent.getAccountNumber(), mview);
            return tuple2(tuple.f0(), mview);
            //return new AbstractMap.SimpleEntry<String,Account>(a.getAcctNumber(), a);
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
