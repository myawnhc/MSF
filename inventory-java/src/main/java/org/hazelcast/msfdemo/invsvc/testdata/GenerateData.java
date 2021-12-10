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

package org.hazelcast.msfdemo.invsvc.testdata;

import org.hazelcast.msfdemo.invsvc.domain.Inventory;
import org.hazelcast.msfdemo.invsvc.domain.Item;
import org.hazelcast.msfdemo.invsvc.views.InventoryDAO;
import org.hazelcast.msfdemo.invsvc.views.ItemDAO;

public class GenerateData {

    // Set up some categories categories
    private static String[] categories = new String[] {
            "Books", "Music", "Furniture", "Sporting Goods", "Men's Clothing",
            "Women's Clothing", "Men's Shoes", "Women's Shoes", "Toys", "Collectibles",
            "Kitchen", "Tools", "Electronics", "Computers", "Pet Supplies",
            "Groceries", "Gardening", "Health", "Cosmetics", "Accessories",
            "Jewelry", "Camera & Photo", "Automotive", "Antiques", "Appliances" };

    // Categories are build as a side effect
    public void generateItems(int count) {
        ItemDAO itemDAO = new ItemDAO();
        int initialItemNumber = 10101;
        for (int i=0; i<count; i++) {
            Item item = new Item();
            String itemNumber = ""+initialItemNumber++;
            item.setItemNumber(itemNumber);
            int catindex = (int) (Math.random()*categories.length);
            item.setCategoryID("C" + catindex);
            item.setCategoryName(categories[catindex]);
            item.setDescription("Item " + itemNumber);
            item.setPrice((int) (Math.random()*10000)); // values to .01 to 999.99
            itemDAO.insert(itemNumber, item);
        }
        System.out.printf("Created %d items\n", count);
        itemDAO.disconnect();
    }
    public static void main(String[] args) {
        // Generate 1000 items
        int numItems = 1000;
        GenerateData main = new GenerateData();
        main.generateItems(numItems);

        // Will use 10 warehouses, location ids 00-09
        // Will use 90 stores, location ids 10-99
        int locations = 100;
        int warehouses = 10; // must be < locations
        main.generateInventory(numItems, locations, warehouses);
        System.exit(0);
    }

    public void generateInventory(int items, int locations, int warehouses) {
        InventoryDAO inventoryDAO = new InventoryDAO();
        // Generate 100K inventory records
        for (int i=0; i<items; i++) {
            String itemNumber = ""+(10101+i);
            for (int l=0; l<locations; l++) {
                Inventory inv = new Inventory();
                inv.setItemNumber(itemNumber);
                if (l<warehouses) {
                    inv.setLocation("W" + l);
                    inv.setLocationType("WH"); // warehouse
                } else {
                    inv.setLocation("S" + l);
                    inv.setLocationType("ST"); // store
                }
                inv.setDescription("Item " + itemNumber);
                inv.setGeohash("not set");
                inv.setQuantityOnHand((int)(Math.random()*1000));
                inv.setQuantityReserved(0);
                inv.setAvailableToPromise(inv.getQuantityOnHand() - inv.getQuantityReserved());
                //System.out.println("Inventory added " + inv.toString());
                inventoryDAO.insert(inv.getKey(), inv);
            }
        }
        System.out.println("Generate inventory data complete");
        inventoryDAO.disconnect();
    }
}
