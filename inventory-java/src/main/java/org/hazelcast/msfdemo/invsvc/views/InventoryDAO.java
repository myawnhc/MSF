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

package org.hazelcast.msfdemo.invsvc.views;

import com.hazelcast.spi.exception.RetryableHazelcastException;
import org.hazelcast.msf.controller.MSFController;
import org.hazelcast.msf.persistence.DAO;
import org.hazelcast.msfdemo.invsvc.domain.Inventory;
import org.hazelcast.msfdemo.invsvc.persistence.InventoryKey;
import org.hazelcast.msfdemo.invsvc.persistence.InventoryMapStore;

public class InventoryDAO extends DAO<Inventory, InventoryKey> {

    public InventoryDAO(MSFController controller) {
        super(controller,"inventory");
        // Create backing table (for MapStore) if not present
        InventoryMapStore mapStore = new InventoryMapStore();

    }

    // Non-inheritable query methods
    public int getInventoryRecordCount() {
        boolean logged = false;
        while (true) {
            try {
                return getMap().size();
            } catch (RetryableHazelcastException rhe) { // probably caught before it reaches us, but let's try ...
                if (!logged) {
                    System.out.println("InventoryDAO.getInventoryRecordCount blocked until data load completes.");
                    logged = true;
                }
            }
        }
    }

}