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
import com.hazelcast.msfdemo.invsvc.persistence.InventoryKey;
import com.hazelcast.msfdemo.invsvc.views.InventoryDAO;
import com.hazelcast.msfdemo.invsvc.views.ItemDAO;
import io.grpc.stub.StreamObserver;

import static com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.*;

public class InventoryAPIImpl extends InventoryGrpc.InventoryImplBase {

    InventoryDAO inventoryDAO = new InventoryDAO();
    ItemDAO itemDAO = new ItemDAO();

    @Override
    public void reserve(ReserveRequest request, StreamObserver<ReserveResponse> responseObserver) {
        String itemNumber = request.getItemNumber();
        String location = request.getLocation();
        int quantity = request.getQuantity();
        System.out.println("Reserve request " + itemNumber + " " + location + " " + quantity);

        // Get ATP from DAO, if not available we fail fast
        InventoryKey invKey = new InventoryKey(itemNumber, location);
        Inventory inv = inventoryDAO.findByKey(invKey);
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
        IMap<InventoryKey,Inventory> invmap = inventoryDAO.getMap();
        invmap.executeOnKey(invKey, (EntryProcessor<InventoryKey, Inventory, Object>) entry -> {
            Inventory iview = entry.getValue();
            iview.setQuantityReserved(iview.getQuantityReserved() + request.getQuantity());
            iview.setAvailableToPromise(iview.getAvailableToPromise() - request.getQuantity());
            // This can happen due to time window since we checked at top of method
            if (iview.getAvailableToPromise() < 0) {
                System.out.println("ATP has gone negative!");
            }
            entry.setValue(iview);
            return iview;
        });

        // Respond to caller
        ReserveResponse success = ReserveResponse.newBuilder()
                .setSuccess(true).build();
        responseObserver.onNext(success);
        responseObserver.onCompleted();
    }

    @Override
    public void pull(PullRequest request, StreamObserver<PullResponse> responseObserver) {
        String itemNumber = request.getItemNumber();
        String location = request.getLocation();
        int quantity = request.getQuantity();
        System.out.println("Pull request " + itemNumber + " " + location + " " + quantity);

        // Get ATP from DAO, if not available we fail fast
        InventoryKey invKey = new InventoryKey(itemNumber, location);
        Inventory inv = inventoryDAO.findByKey(invKey);
        if (inv == null) {
            PullResponse nomatch = PullResponse.newBuilder()
                    .setSuccess(false)
                    .setReason("No record exists for item/location combination")
                    .build();
            responseObserver.onNext(nomatch);
            responseObserver.onCompleted();
            return;
        }

        if (inv.getAvailableToPromise() + inv.getQuantityReserved() < request.getQuantity() ) {
            System.out.printf("Insufficient ATP %d %d %d\n", inv.getQuantityOnHand(), inv.getQuantityReserved(), inv.getAvailableToPromise());
            PullResponse shortage = PullResponse.newBuilder()
                    .setSuccess(false)
                    .setReason(("Insufficient quantity available"))
                    .build();
            responseObserver.onNext(shortage);
            responseObserver.onCompleted();
            return;
        }

        System.out.println("Pull success, updating event store and view");

        // Create Event object
        ReserveInventoryEvent event = new ReserveInventoryEvent();
        event.setItemNumber(request.getItemNumber());
        event.setLocationID(request.getLocation());
        event.setQuantity(request.getQuantity());

        // Persist Event object
        InventoryEventStore store = InventoryEventStore.getInstance();
        store.append(event);

        // Apply changes to DAO
        IMap<InventoryKey,Inventory> invmap = inventoryDAO.getMap();
        invmap.executeOnKey(invKey, (EntryProcessor<InventoryKey, Inventory, Object>) entry -> {
            Inventory iview = entry.getValue();
            // Release from reserved, decrement on hand, recalculate ATP
            iview.setQuantityReserved(iview.getQuantityReserved() - request.getQuantity());
            iview.setQuantityOnHand((iview.getQuantityOnHand() - request.getQuantity()));
            iview.setAvailableToPromise(iview.getQuantityOnHand() - iview.getQuantityReserved());
            if (iview.getAvailableToPromise() < 0) {
                System.out.println("ATP has gone negative!");
            }
            entry.setValue(iview);
            return iview;
        });

        // Respond to caller
        PullResponse success = PullResponse.newBuilder()
                .setSuccess(true).build();
        responseObserver.onNext(success);
        responseObserver.onCompleted();
    }

    @Override
    public void unreserve(ReserveRequest request, StreamObserver<ReserveResponse> responseObserver) {
        System.out.println("unreserve unimplemented in InventoryAPIImpl");
    }

    @Override
    public void restock(PullRequest request, StreamObserver<PullResponse> responseObserver) {
        System.out.println("restock unimplemented in InventoryAPIImpl");
    }

    @Override
    public void getItemCount(ItemCountRequest request, StreamObserver<ItemCountResponse> responseObserver) {
        // Request is empty so ignore it
        ItemCountResponse response = ItemCountResponse.newBuilder()
            .setCount(itemDAO.getItemCount())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getInventoryRecordCount(InventoryCountRequest request, StreamObserver<InventoryCountResponse> responseObserver) {
        // Request is empty so ignore it
        InventoryCountResponse response = InventoryCountResponse.newBuilder()
                .setCount(inventoryDAO.getInventoryRecordCount())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
