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

package org.hazelcast.msfdemo.invsvc.business;

import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.IMap;
import io.grpc.stub.StreamObserver;
import org.hazelcast.msf.controller.MSFController;
import org.hazelcast.msfdemo.invsvc.domain.Inventory;
import org.hazelcast.msfdemo.invsvc.events.InventoryEventStore;
import org.hazelcast.msfdemo.invsvc.events.InventoryGrpc;
import org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass;
import org.hazelcast.msfdemo.invsvc.events.ReserveInventoryEvent;
import org.hazelcast.msfdemo.invsvc.persistence.InventoryKey;
import org.hazelcast.msfdemo.invsvc.views.InventoryDAO;
import org.hazelcast.msfdemo.invsvc.views.ItemDAO;

public class InventoryAPIImpl extends InventoryGrpc.InventoryImplBase {

    final MSFController controller = MSFController.getInstance();

    InventoryDAO inventoryDAO = new InventoryDAO(controller);
    ItemDAO itemDAO = new ItemDAO(controller);

    @Override
    public void reserve(InventoryOuterClass.ReserveRequest request, StreamObserver<InventoryOuterClass.ReserveResponse> responseObserver) {
        String itemNumber = request.getItemNumber();
        String location = request.getLocation();
        int quantity = request.getQuantity();
        System.out.println("Reserve request " + itemNumber + " " + location + " " + quantity);

        // Get ATP from DAO, if not available we fail fast
        InventoryKey invKey = new InventoryKey(itemNumber, location);
        Inventory inv = inventoryDAO.findByKey(invKey);
        if (inv == null) {
            InventoryOuterClass.ReserveResponse nomatch = InventoryOuterClass.ReserveResponse.newBuilder()
                    .setSuccess(false)
                    .setReason("No record exists for item/location combination")
                    .build();
            responseObserver.onNext(nomatch);
            responseObserver.onCompleted();
            return;
        }

        if (inv.getAvailableToPromise() < request.getQuantity()) {
            System.out.printf("Insufficient ATP %d %d %d\n", inv.getQuantityOnHand(), inv.getQuantityReserved(), inv.getAvailableToPromise());
            InventoryOuterClass.ReserveResponse shortage = InventoryOuterClass.ReserveResponse.newBuilder()
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
        InventoryOuterClass.ReserveResponse success = InventoryOuterClass.ReserveResponse.newBuilder()
                .setSuccess(true).build();
        responseObserver.onNext(success);
        responseObserver.onCompleted();
    }

    @Override
    public void pull(InventoryOuterClass.PullRequest request, StreamObserver<InventoryOuterClass.PullResponse> responseObserver) {
        String itemNumber = request.getItemNumber();
        String location = request.getLocation();
        int quantity = request.getQuantity();
        System.out.println("Pull request " + itemNumber + " " + location + " " + quantity);

        // Get ATP from DAO, if not available we fail fast
        InventoryKey invKey = new InventoryKey(itemNumber, location);
        Inventory inv = inventoryDAO.findByKey(invKey);
        if (inv == null) {
            InventoryOuterClass.PullResponse nomatch = InventoryOuterClass.PullResponse.newBuilder()
                    .setSuccess(false)
                    .setReason("No record exists for item/location combination")
                    .build();
            responseObserver.onNext(nomatch);
            responseObserver.onCompleted();
            return;
        }

        if (inv.getAvailableToPromise() + inv.getQuantityReserved() < request.getQuantity() ) {
            System.out.printf("Insufficient ATP %d %d %d\n", inv.getQuantityOnHand(), inv.getQuantityReserved(), inv.getAvailableToPromise());
            InventoryOuterClass.PullResponse shortage = InventoryOuterClass.PullResponse.newBuilder()
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
        InventoryOuterClass.PullResponse success = InventoryOuterClass.PullResponse.newBuilder()
                .setSuccess(true).build();
        responseObserver.onNext(success);
        responseObserver.onCompleted();
    }

    @Override
    public void unreserve(InventoryOuterClass.ReserveRequest request, StreamObserver<InventoryOuterClass.ReserveResponse> responseObserver) {
        System.out.println("unreserve unimplemented in InventoryAPIImpl");
    }

    @Override
    public void restock(InventoryOuterClass.PullRequest request, StreamObserver<InventoryOuterClass.PullResponse> responseObserver) {
        System.out.println("restock unimplemented in InventoryAPIImpl");
    }

    @Override
    public void getItemCount(InventoryOuterClass.ItemCountRequest request, StreamObserver<InventoryOuterClass.ItemCountResponse> responseObserver) {
        // Request is empty so ignore it
        InventoryOuterClass.ItemCountResponse response = InventoryOuterClass.ItemCountResponse.newBuilder()
            .setCount(itemDAO.getItemCount())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getInventoryRecordCount(InventoryOuterClass.InventoryCountRequest request, StreamObserver<InventoryOuterClass.InventoryCountResponse> responseObserver) {
        // Request is empty so ignore it
        InventoryOuterClass.InventoryCountResponse response = InventoryOuterClass.InventoryCountResponse.newBuilder()
                .setCount(inventoryDAO.getInventoryRecordCount())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
