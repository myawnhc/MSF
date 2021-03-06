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

package org.hazelcast.msfdemo.invsvc.persistence;

import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.map.MapStore;
import org.hazelcast.msfdemo.invsvc.domain.Item;
import org.hazelcast.msfdemo.invsvc.service.InventoryService;

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
    private final Connection conn;

    // All inserts, updates need to set origin service into the last-updated-by field;
    // should be a function of the storage layer code and not exposed via user API.
    // CDC will use this to filter out "our" events so we don't cycle endlessly.
    private static final String SERVICE_ORIGIN = InventoryService.SERVICE_NAME;

    private static final String createItemTableString =
            "create table if not exists item ( " +
                    "item_number    varchar(20)     not null, " +
                    "description    varchar(30), " +
                    "category       varchar(4)      not null, " +
                    "price          int, " +
                    "last_updated_by varchar(20), " +
                    "primary key (item_number) " +
                    ")";

    private static final String createCategoryTableString =
            "create table if not exists category ( " +
                    "category_id    varchar(4) not null, " +
                    "description    varchar(20), " +
                    "last_updated_by varchar(20), " +
                    "primary key (category_id) " +
                    ")";

    private static final String insertItemTemplate =
            "replace into item (item_number, description, category, price, last_updated_by) " +
                    " values (?, ?, ?, ?, ?)";

    private static final String insertCategoryTemplate =
            "replace into category (category_id, description, last_updated_by) " +
                    " values (?, ?, ?)";

    private static final String selectItemTemplate =
            "select item_number, description, category, price from item where item_number = ?";

    private static final String selectCategoryTemplate =
            "select category_id, description from category where category_id = ?";

    private static final String selectKeysString = "select item_number from item";

    private static final String deleteAllItemsString = "delete from item";
    private static final String deleteAllCategoriesString = "delete from category";
    private static final String deleteItemString = "delete from item where item_number = ?";

    private PreparedStatement createStatement;
    private PreparedStatement insertCategoryStatement;
    private PreparedStatement insertItemStatement;
    private PreparedStatement selectCategoryStatement;
    private PreparedStatement selectItemStatement;
    private PreparedStatement selectItemKeysStatement;
    private PreparedStatement deleteAllItemsStatement;
    private PreparedStatement deleteAllCategoriesStatement;
    private PreparedStatement deleteItemStatement;

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
            //log.info("Created (if needed) Item table ");
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
            //log.info("Created (if needed) Category table ");
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
            insertCategoryStatement.setString(3, SERVICE_ORIGIN);
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
            insertItemStatement.setString(5, SERVICE_ORIGIN);
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
        //System.out.println("StoreAll complete");
    }

    @Override
    public void delete(String s) {
        try {
            if (deleteItemStatement == null)
                deleteItemStatement = conn.prepareStatement(deleteItemString);
            deleteItemStatement.setString(1, s);
            int rows = deleteItemStatement.executeUpdate();
//            if (rows > 0)
//                System.out.println("Deleted " + s + " from item table");
//            else
//                System.out.println("No record " + s + " to delete in item table");
        } catch (SQLException sqe) {
            sqe.printStackTrace();
        }

    }

    @Override
    public void deleteAll(Collection<String> collection) {
        System.out.println("DeleteAll items with " + collection.size() + " items");
        for (String key : collection)
            delete(key);
    }

    // NON-API
    public void deleteAllItems() {
        // Note that this deletes everything, not just the items in the collection!
        try {
            if (deleteAllItemsStatement == null)
                deleteAllItemsStatement = conn.prepareStatement(deleteAllItemsString);
            int rows = deleteAllItemsStatement.executeUpdate();
            System.out.println(rows + " items deleted from InventoryDB by ItemMapStore");
            if (deleteAllCategoriesStatement == null)
                deleteAllCategoriesStatement = conn.prepareStatement(deleteAllCategoriesString);
            rows = deleteAllCategoriesStatement.executeUpdate();
            System.out.println(rows + " categories deleted from InventoryDB by ItemMapStore");
        } catch (SQLException sqe) {
            sqe.printStackTrace();
        }
        //System.out.println("deleteAll");
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
