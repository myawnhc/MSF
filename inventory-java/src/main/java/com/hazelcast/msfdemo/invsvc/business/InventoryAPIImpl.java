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

package com.hazelcast.msfdemo.invsvc.business;

import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.IMap;
import com.hazelcast.msfdemo.invsvc.domain.Inventory;
import com.hazelcast.msfdemo.invsvc.events.InventoryEventStore;
import com.hazelcast.msfdemo.invsvc.events.InventoryGrpc;
import com.hazelcast.msfdemo.invsvc.events.ReserveInventoryEvent;
import com.hazelcast.msfdemo.invsvc.views.InventoryDAO;
import io.grpc.stub.StreamObserver;

import static com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest;
import static com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveResponse;

public class InventoryAPIImpl extends InventoryGrpc.InventoryImplBase {

    InventoryDAO dao = new InventoryDAO();

    @Override
    public void reserve(ReserveRequest request, StreamObserver<ReserveResponse> responseObserver) {
        String itemNumber = request.getItemNumber();
        String location = request.getLocation();
        int quantity = request.getQuantity();
        System.out.println("Reserve request " + itemNumber + " " + location + " " + quantity);

        // Get ATP from DAO, if not available we fail fast
        String invKey = itemNumber + location;
        Inventory inv = dao.findByKey(invKey);
        if (inv == null) {
            ReserveResponse nomatch = ReserveResponse.newBuilder()
                    .setSuccess(false)
                    .setReason("No record exists for item/location combination")
                    .build();
            responseObserver.onNext(nomatch);
            responseObserver.onCompleted();
            return;
        }

        if (inv.getAvailableToPromise() < request.getQuantity()) {
            System.out.printf("Insufficient ATP %d %d %d\n", inv.getQuantityOnHand(), inv.getQuantityReserved(), inv.getAvailableToPromise());
            ReserveResponse shortage = ReserveResponse.newBuilder()
                    .setSuccess(false)
                    .setReason(("Insufficient quantity available"))
                    .build();
            responseObserver.onNext(shortage);
            responseObserver.onCompleted();
            return;
        }

        System.out.println("Reserve success, updating event store and view");

        // Create Event object
        ReserveInventoryEvent event = new ReserveInventoryEvent();
        event.setItemNumber(request.getItemNumber());
        event.setLocationID(request.getLocation());
        event.setQuantity(request.getQuantity());

        // Persist Event object
        InventoryEventStore store = InventoryEventStore.getInstance();
        store.append(event);

        // Apply changes to DAO
        IMap<String,Inventory> invmap = dao.getMap();
        invmap.executeOnKey(invKey, (EntryProcessor<String, Inventory, Object>) entry -> {
            Inventory iview = entry.getValue();
            iview.setQuantityReserved(iview.getQuantityReserved() + request.getQuantity());
            iview.setAvailableToPromise(iview.getAvailableToPromise() - request.getQuantity());
            // This can happen due to time window since we checked at top of method
            if (iview.getAvailableToPromise() < 0) {
                System.out.println("ATP has gone negative!");
            }
            return iview;
        });

        // Respond to caller
        ReserveResponse success = ReserveResponse.newBuilder()
                .setSuccess(true).build();
        responseObserver.onNext(success);
        responseObserver.onCompleted();
    }

//    @Override
//    public void reserve(ReserveRequest request, StreamObserver<ReserveResponse> responseObserver) {
//        // Unique ID used to pair up requests with responses
//        long uniqueID = controller.getUniqueMessageID();
//
//        // Get listener to result map armed before we trigger the pipeline
//        UUID listenerID = reservePipelineOutput.addEntryListener((EntryAddedListener<Long, APIResponse<InventoryEvent>>) entryEvent -> {
//            //System.out.println("OPEN completion listener fired for ID " + uniqueID);
//            APIResponse<InventoryEvent> apiResponse = entryEvent.getValue();
//            if (apiResponse.getStatus() == APIResponse.Status.SUCCESS) {
//                // Should specifically be an InventoryReserveEvent
//                InventoryEvent event = apiResponse.getResultValue();
//                ReserveResponse grpcResponse =
//                        ReserveResponse.newBuilder()
//                                .setSuccess(true)
//                                .build();
//                responseObserver.onNext(grpcResponse);
//            } else {
//                responseObserver.onError(apiResponse.getError());
//            }
//
//
//            responseObserver.onCompleted();
//            reservePipelineOutput.remove(uniqueID);
//            reservePipelineInput.remove(uniqueID);
//            // Remove ourself as a listener.  Have to do this indirection of getting from map because
//            // otherwise we get 'listenerID may not have been initialized'.
//            UUID myID = listenersByRequestID.remove(uniqueID);
//            if (myID == null) {
//                System.out.println("InventoryAPIImpl.reserve handler - listener for " + uniqueID + " was already removed");
//            } else {
//                reservePipelineOutput.removeEntryListener(myID);
//            }
//
//        }, Predicates.sql("__key=" + uniqueID), true);
//
//        UUID oldID = listenersByRequestID.put(uniqueID, listenerID);
//        if (oldID != null)
//            System.out.println("ERROR: Multiple requests with same ID! " + oldID + " (seen in reserve)");
//        // Pass the request into the ReserveInventoryHandler pipeline
//        reservePipelineInput.set(uniqueID, request);
//    }
}
