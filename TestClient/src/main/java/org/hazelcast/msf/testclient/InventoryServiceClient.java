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

package org.hazelcast.msf.testclient;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.hazelcast.msf.configuration.ServiceConfig;
import org.hazelcast.msfdemo.invsvc.events.InventoryGrpc;
import org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class InventoryServiceClient {
    private static final Logger logger = Logger.getLogger(InventoryServiceClient.class.getName());
    private InventoryGrpc.InventoryBlockingStub blockingStub;
    private ManagedChannel channel;


    public ManagedChannel initChannel() {

        ServiceConfig.ServiceProperties props = ServiceConfig.get("inventory-service");
        String target = props.getTarget();
        logger.info("Target from service.yaml.test " + target);


        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                .usePlaintext()
                .build();

        blockingStub = InventoryGrpc.newBlockingStub(channel);

        return channel;
    }

    private void shutdownChannel() {
        try {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            ;
        }
    }

    public int getItemCount() {
        if (blockingStub == null)
            initChannel();

        InventoryOuterClass.ItemCountRequest request = InventoryOuterClass.ItemCountRequest.newBuilder()
                .build();

        InventoryOuterClass.ItemCountResponse response = blockingStub.getItemCount(request);
        return response.getCount();
    }

    public int getInventoryRecordCount() {
        if (blockingStub == null)
            initChannel();

        InventoryOuterClass.InventoryCountRequest request = InventoryOuterClass.InventoryCountRequest.newBuilder()
                .build();

        InventoryOuterClass.InventoryCountResponse response = blockingStub.getInventoryRecordCount(request);
        return response.getCount();
    }

}
