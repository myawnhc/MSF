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

import org.hazelcast.msf.controller.MSFController;
import org.hazelcast.msf.persistence.DAO;
import org.hazelcast.msfdemo.invsvc.domain.Item;
import org.hazelcast.msfdemo.invsvc.persistence.ItemMapStore;

public class ItemDAO extends DAO<Item,String> {

    public ItemDAO(MSFController controller) {
        super(controller, "item");
        // Creates backing tables (for MapStore) if not present
        ItemMapStore mapStore = new ItemMapStore();
    }

    public int getItemCount() {
        return getMap().size();
    }
}
