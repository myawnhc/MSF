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

package com.hazelcast.msfdemo.catalogsvc.business;

import com.hazelcast.msfdemo.protosvc.events.CatalogGrpc;
import com.hazelcast.msfdemo.protosvc.events.CatalogOuterClass;
import com.hazelcast.msfdemo.protosvc.events.CatalogOuterClass.PriceLookupResponse;
import io.grpc.stub.StreamObserver;

public class CatalogAPIImpl extends CatalogGrpc.CatalogImplBase {

    @Override
    public void priceLookup(CatalogOuterClass.PriceLookupRequest request, StreamObserver<PriceLookupResponse> responseObserver) {
        // This is intended to be a non-Java service, so not doing Jet pipelines.
        // For now we just mock the service
        String itemNumber = request.getItemNumber();

        PriceLookupResponse grpcResponse =
                PriceLookupResponse.newBuilder()
                        .setPrice(1000)
                        .build();
        responseObserver.onNext(grpcResponse);
        responseObserver.onCompleted();
    }
}