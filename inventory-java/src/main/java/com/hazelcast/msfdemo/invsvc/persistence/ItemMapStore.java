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

package com.hazelcast.msfdemo.invsvc.persistence;

import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.map.MapStore;
import com.hazelcast.msfdemo.invsvc.domain.Item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemMapStore implements MapStore<String, Item> {

    private final static ILogger log = Logger.getLogger(ItemMapStore.class);
    private Connection conn;

    private static final String createItemTableString =
            "create table if not exists item ( " +
                    "item_number    varchar(20)     not null, " +
                    "description    varchar(30), " +
                    "category       varchar(4)      not null, " +
                    "price          int, " +
                    "primary key (item_number) " +
//                    "foreign key (category) " +
//                    "   references category (category_id) " +
                    ")";

    private static final String createCategoryTableString =
            "create table if not exists category ( " +
                    "category_id    varchar(4) not null, " +
                    "description    varchar(20), " +
                    "primary key (category_id) " +
                    ")";

    private static final String insertItemTemplate =
            "insert into item (item_number, description, category, price) " +
                    " values (?, ?, ?, ?)";

    private static final String insertCategoryTemplate =
            "insert into category (category_id, description) " +
                    " values (?, ?)";

    private static final String selectItemTemplate =
            "select item_number, description, category, price from item where item_number = ?";

    private static final String selectCategoryTemplate =
            "select category_id, description from category where category_id = ?";

    private static final String selectKeysString = "select item_number from item";

    private PreparedStatement createStatement;
    private PreparedStatement insertCategoryStatement;
    private PreparedStatement insertItemStatement;
    private PreparedStatement selectCategoryStatement;
    private PreparedStatement selectItemStatement;
    private PreparedStatement selectItemKeysStatement;

    public ItemMapStore() {
        // Connect and create database if it doesn't yet exist
        InventoryDB database = new InventoryDB();
        database.establishConnection();
        database.createDatabase();
        database.createUser();

        // Connect to the database just created
        conn = DBConnection.establishConnection();

        // These will only create if tables do not yet exist.  If restructuring,
        // just shut down the docker image to build a fresh copy of the DB.
        createCategoryTable();
        createItemTable();
    }

    public synchronized void createItemTable()  {
        try {
            createStatement = conn.prepareStatement(createItemTableString);
            createStatement.executeUpdate();
            createStatement.close();
            log.info("Created Item table ");
        } catch (SQLException se) {
            se.printStackTrace();
            System.exit(-1);
        }
    }

    public synchronized void createCategoryTable()  {
        try {
            createStatement = conn.prepareStatement(createCategoryTableString);
            createStatement.executeUpdate();
            createStatement.close();
            log.info("Created Category table ");
        } catch (SQLException se) {
            se.printStackTrace();
            System.exit(-1);
        }
    }

    // In our services, category data is kept as part of the Item, so we don't expose
    // a separate domain class for it.  We have it here as a convenience in moving data
    // in and out of the database.
    private static class Category {
        public String categoryID;
        public String description;
    }

    // May return null
    private synchronized Category readCategory(String catid) {
        try {
            if (selectCategoryStatement == null) {
                selectCategoryStatement = conn.prepareStatement(selectCategoryTemplate);
            }
            selectCategoryStatement.setString(1, catid);
            ResultSet rs = selectCategoryStatement.executeQuery();
            if (rs == null) return null;
            while (rs.next()) {
                Category cat = new Category();
                cat.categoryID = rs.getString(1);
                cat.description = rs.getString(2);
                return cat;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // May return null
    private synchronized Item readItem(String itemNumber) {
        try {
            if (selectItemStatement == null) {
                selectItemStatement = conn.prepareStatement(selectItemTemplate);
            }
            selectItemStatement.setString(1, itemNumber);
            ResultSet rs = selectItemStatement.executeQuery();
            if (rs == null) return null;
            while (rs.next()) {
                Item item = new Item();
                item.setItemNumber( rs.getString(1));
                item.setDescription( rs.getString(2));
                item.setCategoryID(rs.getString(3));
                Category cat = readCategory(item.getCategoryID());
                item.setCategoryName(cat.description);
                item.setPrice(rs.getInt(4));
                return item;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private synchronized void writeCategory(Category c) {
        try {
            if (insertCategoryStatement == null)
                insertCategoryStatement = conn.prepareStatement(insertCategoryTemplate);
            insertCategoryStatement.setString(1, c.categoryID);
            insertCategoryStatement.setString(2, c.description);
            int rowsAffected = insertCategoryStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private synchronized void writeItem(Item item) {
        try {
            if (insertItemStatement == null)
                insertItemStatement = conn.prepareStatement(insertItemTemplate);
            insertItemStatement.setString(1, item.getItemNumber());
            insertItemStatement.setString(2, item.getDescription());
            insertItemStatement.setString(3, item.getCategoryID());
            insertItemStatement.setInt(4, item.getPrice());
            int rowsAffected = insertItemStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //////////////////////////////
    // MapStore implementation
    //////////////////////////////

    @Override
    public void store(String s, Item item) {
        //System.out.printf("Store %s %s\n", s, item);
        // Insert entry into Category table if it does not exist
        Category c = readCategory(item.getCategoryID());
        if (c == null) {
            c = new Category();
            c.categoryID = item.getCategoryID();
            c.description = item.getCategoryName();
            writeCategory(c);
        }
        writeItem(item);
    }

    @Override
    public void storeAll(Map<String, Item> map) {
        System.out.println("StoreAll called with " + map.size() + " items");
        for (Item item : map.values()) {
            store(item.getItemNumber(), item);
        }
        System.out.println("StoreAll complete");
    }

    @Override
    public void delete(String s) {
        System.out.printf("Delete %s", s);
    }

    @Override
    public void deleteAll(Collection<String> collection) {
        System.out.println("deleteAll");
    }

    //////////////////////////////
    // MapLoader implementation
    //////////////////////////////

    @Override
    public Item load(String s) {
        // Note that writeBehindStore calls load
        //System.out.printf("load %s\n", s);
        return readItem(s);
    }

    @Override
    public Map<String, Item> loadAll(Collection<String> collection) {
        //System.out.println("loadAll");
        Map<String, Item> items = new HashMap<>();
        for (String key : collection) {
            Item value = load(key);
            if (value != null)
                items.put(key, value);
        }
        //System.out.println("Loaded " + items.size() + " items from " + collection.size() + " keys");
        return items;
    }

    @Override
    public Iterable<String> loadAllKeys() {
        //System.out.println("loadAllKeys");
        List<String> keys = new ArrayList<>();
        try {
            if (selectItemKeysStatement == null) {
                selectItemKeysStatement = conn.prepareStatement(selectKeysString);
            }
            ResultSet rs = selectItemKeysStatement.executeQuery();

            if (rs == null) return null;
            while (rs.next()) {
                keys.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //System.out.println("Returning " + keys.size() + " keys from Item table");
        return keys;
    }
}
