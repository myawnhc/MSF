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

package com.hazelcast.msfdemo.invsvc.views;

import com.hazelcast.msf.persistence.DAO;
import com.hazelcast.msfdemo.invsvc.domain.Inventory;
import com.hazelcast.msfdemo.invsvc.persistence.InventoryKey;
import com.hazelcast.msfdemo.invsvc.persistence.InventoryMapStore;

public class InventoryDAO extends DAO<Inventory, InventoryKey> {

    public InventoryDAO() {
        super("inventory");
        // Create backing table (for MapStore) if not present
        InventoryMapStore mapStore = new InventoryMapStore();

    }

    // Non-inheritable query methods
    public int getInventoryRecordCount() {
        System.out.println("InventoryDAO.getInventoryRecordCount getting map size - MapLoader should bel LAZY but throws!");
        return getMap().size();
    }

}