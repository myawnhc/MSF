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

import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.retry.IntervalFunction;
import com.hazelcast.jet.retry.RetryStrategy;
import com.hazelcast.msf.controller.MSFController;
import com.hazelcast.msfdemo.invsvc.service.InventoryService;

import java.io.File;

import static com.hazelcast.jet.cdc.mysql.MySqlCdcSources.mysql;

// TODO: Because we want a non-Java implementation of the Inventory service, I
//  want to rewrite this pipeline using SQL
public class CDCPipeline implements Runnable {

    @Override
    public void run() {
        try {
            MSFController controller = MSFController.getInstance();
            File f = new File("./account/target/AccountService-1.0-SNAPSHOT.jar");
            System.out.println("CDCPipeline.run() invoked, submitting job");
            controller.startJob("InventoryService", "InventoryService.CDCPipeline", f, createPipeline());
        } catch (Exception e) { // Happens if our pipeline is not valid
            e.printStackTrace();
        }
    }

    private static class Retry3Strategy implements RetryStrategy {

        @Override
        public int getMaxAttempts() {
            return 3;
        }

        @Override
        public IntervalFunction getIntervalFunction() {
            return IntervalFunction.constant(5000); // milliseconds
        }
    }

    private static Pipeline createPipeline() {
        Pipeline p = Pipeline.create();
        p.readFrom(
                // Name needs to be unique so using service rather than db; passed to Kafka
                mysql("invservice")
                        .setDatabaseAddress("invdb")
                        .setDatabasePort(3306)
                        .setDatabaseUser("invuser")
                        .setDatabasePassword("invpass")
                        .setClusterName("invdb")
                        .setDatabaseWhitelist("InventoryDB")
                        .setReconnectBehavior(new Retry3Strategy())
                        .build())
                .withNativeTimestamps(0)
                // Filter out events with local origin, we have already applied them
                .filter(changeRecord -> !changeRecord.value().toMap().get("last_updated_by").equals(InventoryService.SERVICE_NAME))
                // Confirmed we now just see OrderService stuff, but it's spamming the log so let's
                // throw it away until we're ready to process it.
                .writeTo(Sinks.noop());
        return p;
    }
}
