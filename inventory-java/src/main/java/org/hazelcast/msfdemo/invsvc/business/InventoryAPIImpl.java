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
import io.grpc.Context;
import io.grpc.stub.StreamObserver;
import org.hazelcast.msf.controller.MSFController;
import org.hazelcast.msfdemo.invsvc.domain.Inventory;
import org.hazelcast.msfdemo.invsvc.domain.Item;
import org.hazelcast.msfdemo.invsvc.events.InventoryEventStore;
import org.hazelcast.msfdemo.invsvc.events.InventoryGrpc;
import org.hazelcast.msfdemo.invsvc.events.PullInventoryEvent;
import org.hazelcast.msfdemo.invsvc.events.ReserveInventoryEvent;
import org.hazelcast.msfdemo.invsvc.persistence.InventoryKey;
import org.hazelcast.msfdemo.invsvc.views.InventoryDAO;
import org.hazelcast.msfdemo.invsvc.views.ItemDAO;

import static org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.*;

public class InventoryAPIImpl extends InventoryGrpc.InventoryImplBase {

    final MSFController controller = MSFController.getInstance();
    InventoryDAO inventoryDAO = new InventoryDAO(controller);
    ItemDAO itemDAO = new ItemDAO(controller);

    private int unacknowledgedAddInventoryRequests = 0;
    private int airBatchSize = 1000;

    public InventoryAPIImpl() {
        // Events need instance to support pub/sub functionality
        ReserveInventoryEvent.setHazelcastInstance(controller.getHazelcastInstance());
        PullInventoryEvent.setHazelcastInstance(controller.getHazelcastInstance());
    }

    @Override
    public void clearAllData(ClearAllDataRequest request, StreamObserver<ClearAllDataResponse> response) {
        Context ctx = Context.current().fork();
        ctx.run(() -> {
            inventoryDAO.deleteAll(); // affects MapStore as well
            itemDAO.deleteAll(); // affects MapStore as well
        });
        response.onNext(ClearAllDataResponse.newBuilder().build());
        // TODO: should wait until deletes propagate to the DB before marking complete
        response.onCompleted();
        System.out.println("*** All Item & Inventory Data cleared ***");
    }

    @Override
    public StreamObserver<AddItemRequest> addItem(StreamObserver<AddItemResponse> response) {
        return new StreamObserver<AddItemRequest>() {
            @Override
            public void onNext(AddItemRequest addItemRequest) {
                Item item = new Item();
                item.setItemNumber(addItemRequest.getItemNumber());
                item.setDescription(addItemRequest.getDescription());
                item.setCategoryID(addItemRequest.getCategoryID());
                item.setCategoryName(addItemRequest.getCategoryName());
                item.setPrice(addItemRequest.getPrice());
                itemDAO.insert(item.getItemNumber(), item);
                //System.out.println("InventoryAPIItem.addItem inserted " + item.getItemNumber());
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("InvAPI AddItemRequest.onError:");
                throwable.printStackTrace();
            }

            @Override
            public void onCompleted() {
                // Empty response
                response.onNext(AddItemResponse.newBuilder().build());
                response.onCompleted();
                System.out.println("InventoryAPIImpl.addItem.onCompleted: all items done");
            }
        };
    }

    @Override
    public StreamObserver<AddInventoryRequest> addInventory(StreamObserver<AddInventoryResponse> response) {
        //Context ctx = Context.current().fork(); // attempt to avoid 'client cancelled' errors
        return new StreamObserver<>() {
            @Override
            public void onNext(AddInventoryRequest addInventoryRequest) {
                //ctx.run(() -> {
                unacknowledgedAddInventoryRequests++;
                Inventory stock = new Inventory();
                stock.setItemNumber(addInventoryRequest.getItemNumber());
                stock.setDescription(addInventoryRequest.getDescription());
                stock.setLocation(addInventoryRequest.getLocation());
                stock.setLocationType(addInventoryRequest.getLocationType());
                stock.setGeohash(addInventoryRequest.getGeohash());
                stock.setQuantityOnHand(addInventoryRequest.getQtyOnHand());
                stock.setQuantityReserved(addInventoryRequest.getQtyReserved());
                stock.setAvailableToPromise(addInventoryRequest.getAvailToPromise());
                InventoryKey key = new InventoryKey(stock.getItemNumber(), stock.getLocation());
                inventoryDAO.insert(key, stock);
                if (unacknowledgedAddInventoryRequests > airBatchSize) {
                    AddInventoryResponse batchAck = AddInventoryResponse.newBuilder().setAckCount(airBatchSize).build();
                    response.onNext(batchAck);
                    unacknowledgedAddInventoryRequests -= airBatchSize;
                    //System.out.println("Acknowledged " + airBatchSize + " AddInventory requests, " + unacknowledgedAddInventoryRequests + "  still in flight");
                }
                //});
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("InvAPI AddInventoryRequest.onError:");
                throwable.printStackTrace();
            }

            @Override
            public void onCompleted() {
                // Empty response
                response.onNext(AddInventoryResponse.newBuilder().build());
                response.onCompleted();
                System.out.println("InventoryAPIImpl.addInventory complete");
            }
        };
    }

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
        InventoryEventStore store = new InventoryEventStore(controller.getHazelcastInstance());
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
        InventoryEventStore store = new InventoryEventStore(controller.getHazelcastInstance());
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
