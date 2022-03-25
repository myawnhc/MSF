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
package org.hazelcast.msf.dataload;

import java.util.logging.Logger;

public class DataLoadMain {
    private static final Logger logger = Logger.getLogger(DataLoadMain.class.getName());

    public static void main(String[] args) {
        DataLoadMain main = new DataLoadMain();
        main.createAccounts();
        main.createInventory();
    }

    public DataLoadMain() { }

    private void createAccounts() {
        AccountDataGenerator asc = new AccountDataGenerator();
        try {
            // Expect no accounts initially, but check in case we later make account info
            // persistent.
            int validAccounts = asc.getNumberOfAccounts();
            if (validAccounts == 0) {
                logger.info("Initializing 1000 test accounts");
                asc.openTestAccounts(1000);
                validAccounts = asc.getNumberOfAccounts();
                logger.info("After test data init, have " + validAccounts + " accounts");
            }
        } catch (io.grpc.StatusRuntimeException e) {
            e.printStackTrace();
        }
    }

    private void createInventory() {
        InventoryDataGenerator igen = new InventoryDataGenerator();
        int currentItemCount = igen.getItemCount();
        System.out.println("Current item count is " + currentItemCount);
        int currentInventoryCount = igen.getInventoryCount();
        System.out.println("Current inventory record count is " + currentInventoryCount);
//        // TODO: what if non-zero count but < expected 1K entries?
//        if (currentItemCount > 0 && currentItemCount < 1000) {
//            logger.info("*** INCOMPLETE inventory data; will drop and recreate");
//            igen.resetData();
//        }
//        currentItemCount = igen.getItemCount();
//        System.out.println("Refreshed: Current item count is " + currentItemCount);
        if (currentItemCount == 0) {
            try {
                logger.info("Initializing 1000 inventory items");
                igen.createItems(1000);
                // create items will trigger the associated inventory records creation
                logger.info("Inventory items initialized");
                igen.createStock();
                logger.info("Inventory stock records initialized");

                while (! igen.dataloadFinished()) {
                    System.out.println("DataLoadMain waiting for inventory load to finish ");
                    Thread.sleep(5000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Items already present, nothing generated");
        }
    }
}
