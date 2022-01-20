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

package org.hazelcast.msfdemo.catalogsvc.business;

import io.grpc.stub.StreamObserver;
import org.hazelcast.msfdemo.protosvc.events.CatalogGrpc;
import org.hazelcast.msfdemo.protosvc.events.CatalogOuterClass;
import org.hazelcast.msfdemo.protosvc.events.CatalogOuterClass.PriceLookupResponse;

public class CatalogAPIImpl extends CatalogGrpc.CatalogImplBase {

    @Override
    public void priceLookup(CatalogOuterClass.PriceLookupRequest request, StreamObserver<PriceLookupResponse> responseObserver) {
        // This is intended to be a non-Java service, so not doing Jet pipelines.
        // -- although if we can do a Jet pipeline in streaming SQL we should go that route.
        // For now we just mock the service; this was mocked before the InventoryDB was
        // in place, we could now look up actual price from the Item table.
        String itemNumber = request.getItemNumber();

        System.out.println("Performing priceLookup for item " + itemNumber);
        PriceLookupResponse grpcResponse =
                PriceLookupResponse.newBuilder()
                        .setPrice(1000)
                        .build();
        responseObserver.onNext(grpcResponse);
        responseObserver.onCompleted();
    }
}