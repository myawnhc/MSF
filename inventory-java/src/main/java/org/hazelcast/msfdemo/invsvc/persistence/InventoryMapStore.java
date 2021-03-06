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
import org.hazelcast.msfdemo.invsvc.domain.Inventory;
import org.hazelcast.msfdemo.invsvc.domain.Item;
import org.hazelcast.msfdemo.invsvc.service.InventoryService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryMapStore implements MapStore<InventoryKey, Inventory> {

    private final static ILogger log = Logger.getLogger(InventoryMapStore.class);
    private Connection conn;

    // All inserts, updates need to set origin service into the last-updated-by field;
    // should be a function of the storage layer code and not exposed via user API.
    // CDC will use this to filter out "our" events so we don't cycle endlessly.
    private static final String SERVICE_ORIGIN = InventoryService.SERVICE_NAME;

    private static final String createInventoryTableString =
            "create table if not exists inventory ( " +
                    "item_number    varchar(20)     not null, " +
                    "location       varchar(4)      not null, " +
                    "quantity       int, " +
                    "reserved       int, " +
                    "atp            int, " +
                    "last_updated_by varchar(20), " +
                    "primary key (item_number, location) " +
                    ")";

    private static final String createLocationTableString =
            "create table if not exists location ( " +
                    "location_id    varchar(4) not null, " +
                    "location_type  varchar(2), " +
                    "geohash        varchar(10), " +
                    "last_updated_by varchar(20), " +
                    "primary key (location_id) " +
                    ")";

    private static final String insertInventoryTemplate =
            "replace into inventory (item_number, location, quantity, reserved, atp, last_updated_by) " +
                    " values (?, ?, ?, ?, ?, ?)";

    private static final String insertLocationTemplate =
            "replace into location (location_id, location_type, geohash, last_updated_by) " +
                    " values (?, ?, ?, ?)";

    private static final String selectInventoryTemplate =
            "select item_number, location, quantity, reserved, atp from inventory where item_number = ? and location = ?";

    private static final String updateInventoryTemplate =
            "update inventory set quantity=?, reserved=?, atp=?, last_updated_by=?" +
                    " where item_number = ? and location = ?";

    private static final String selectItemTemplate =
            "select description from item where item_number = ?";

    private static final String selectLocationTemplate =
            "select location_id, location_type, geohash from location where location_id = ?";

    private static final String selectKeysString = "select item_number, location from inventory";

    private static final String deleteAllInventoryString = "delete from inventory";
    private static final String deleteAllLocationsString = "delete from location";
    private static final String deleteInventoryString = "delete from inventory where item_number = ? and location = ?";

    private PreparedStatement createStatement;
    private PreparedStatement insertInventoryStatement;
    private PreparedStatement insertLocationStatement;
    private PreparedStatement selectInventoryStatement;
    private PreparedStatement selectLocationStatement;
    private PreparedStatement selectItemStatement;
    private PreparedStatement selectItemKeysStatement;
    private PreparedStatement updateInventoryStatement;
    private PreparedStatement deleteAllInventoryStatement;
    private PreparedStatement deleteAllLocationsStatement;
    private PreparedStatement deleteInventoryStatement;

    public InventoryMapStore() {
        // Connect and create database if it doesn't yet exist
        InventoryDB database = new InventoryDB();
        database.establishConnection();
        database.createDatabase();
        database.createUser();

        // Connect to the database just created
        conn = DBConnection.establishConnection();

        // These will only create if tables do not yet exist.  If restructuring,
        // just shut down the docker image to build a fresh copy of the DB.
        createInventoryTable();
        createLocationTable();
    }

    public synchronized void createInventoryTable()  {
        try {
            createStatement = conn.prepareStatement(createInventoryTableString);
            createStatement.executeUpdate();
            createStatement.close();
            //log.info("Created (if needed) Inventory table ");
        } catch (SQLException se) {
            se.printStackTrace();
            System.exit(-1);
        }
    }

    public synchronized void createLocationTable()  {
        try {
            createStatement = conn.prepareStatement(createLocationTableString);
            createStatement.executeUpdate();
            createStatement.close();
            //log.info("Created (if needed) Location table ");
        } catch (SQLException se) {
            se.printStackTrace();
            System.exit(-1);
        }
    }

    // In our services, location data is kept as part of the Inventory, so we don't expose
    // a separate domain class for it.  We have it here as a convenience in moving data
    // in and out of the database.
    private static class Location {
        public String locationID;
        public String locationType; // W warehouse or S store
        public String geohash;
    }

    // May return null
    private synchronized Location readLocation(String locationID) {
        try {
            if (selectLocationStatement == null) {
                selectLocationStatement = conn.prepareStatement(selectLocationTemplate);
            }
            selectLocationStatement.setString(1, locationID);
            ResultSet rs = selectLocationStatement.executeQuery();
            if (rs == null) return null;
            while (rs.next()) {
                Location loc = new Location();
                loc.locationID = rs.getString(1);
                loc.locationType = rs.getString(2);
                loc.geohash = rs.getString(3);
                return loc;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // May return null.  Returns only Description, not full Item object.
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
                item.setDescription( rs.getString(1));
                return item;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // May return null
    private synchronized Inventory readInventory(String itemNumber, String location) {
        try {
            if (selectInventoryStatement == null) {
                selectInventoryStatement = conn.prepareStatement(selectInventoryTemplate);
            }
            selectInventoryStatement.setString(1, itemNumber);
            selectInventoryStatement.setString(2, location);
            ResultSet rs = selectInventoryStatement.executeQuery();
            if (rs == null) return null;
            while (rs.next()) {
                Inventory inv = new Inventory();
                inv.setItemNumber( rs.getString(1));
                Item item = readItem(itemNumber);
                if (item == null) {
                    //System.out.println("No item record for " + itemNumber);
                    return null;
                }
                inv.setDescription(item.getDescription());
                Location loc = readLocation(location);
                if (loc == null) {
                    System.out.println("No location record for " + location + " referenced by item " + itemNumber + " " + item.getDescription());
                    return null;
                } else {
                    inv.setLocation(loc.locationID);
                    inv.setLocationType(loc.locationType);
                    inv.setGeohash(loc.geohash);
                }
                inv.setQuantityOnHand(rs.getInt(3));
                inv.setQuantityReserved(rs.getInt(4));
                inv.setAvailableToPromise(rs.getInt(5));
                return inv;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private synchronized void writeLocation(Location loc) {
        try {
            if (insertLocationStatement == null)
                insertLocationStatement = conn.prepareStatement(insertLocationTemplate);
            insertLocationStatement.setString(1, loc.locationID);
            insertLocationStatement.setString(2, loc.locationType);
            insertLocationStatement.setString(3, loc.geohash);
            insertLocationStatement.setString(4, SERVICE_ORIGIN);
            int rowsAffected = insertLocationStatement.executeUpdate();
            //System.out.println("writeLocation rows " + rowsAffected);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private synchronized void writeInventory(Inventory inv) {
        try {
            if (insertInventoryStatement == null)
                insertInventoryStatement = conn.prepareStatement(insertInventoryTemplate);
            insertInventoryStatement.setString(1, inv.getItemNumber());
            insertInventoryStatement.setString(2, inv.getLocation());
            insertInventoryStatement.setInt(3, inv.getQuantityOnHand());
            insertInventoryStatement.setInt(4, inv.getQuantityReserved());
            insertInventoryStatement.setInt(5, inv.getAvailableToPromise());
            insertInventoryStatement.setString(6, SERVICE_ORIGIN);
            int rowsAffected = insertInventoryStatement.executeUpdate();
            //System.out.println("writeInventory rows " + rowsAffected);
        } catch (SQLIntegrityConstraintViolationException e) {
            // Update; only allow change to the 3 quantity fields.
            try {
                if (updateInventoryStatement == null)
                    updateInventoryStatement = conn.prepareStatement(updateInventoryTemplate);
                updateInventoryStatement.setInt(1, inv.getQuantityOnHand());
                updateInventoryStatement.setInt(2, inv.getQuantityReserved());
                updateInventoryStatement.setInt(3, inv.getAvailableToPromise());
                updateInventoryStatement.setString(4, SERVICE_ORIGIN);
                updateInventoryStatement.setString(5, inv.getItemNumber());
                updateInventoryStatement.setString(6, inv.getLocation());
                int rowsAffected = updateInventoryStatement.executeUpdate();
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //////////////////////////////
    // MapStore implementation
    //////////////////////////////

    @Override
    public void store(InventoryKey key, Inventory inv) {
        //System.out.printf("Store %s %s\n", key.itemNumber, key.locationID, inv);
        // Insert entry into Location table if it does not exist
        Location loc = readLocation(inv.getLocation());
        if (loc == null) {
            loc = new Location();
            loc.locationID = inv.getLocation();
            loc.locationType = inv.getLocationType();
            loc.geohash = inv.getGeohash();
            writeLocation(loc);
        }
        writeInventory(inv);
    }

    @Override
    public void storeAll(Map<InventoryKey, Inventory> map) {
        System.out.println("StoreAll called with " + map.size() + " items");
        for (Inventory inv : map.values()) {
            InventoryKey key = new InventoryKey(inv.getItemNumber(), inv.getLocation());
            store(key, inv);
        }
        //System.out.println("StoreAll complete");
    }

    @Override
    public void delete(InventoryKey key) {
        try {
            if (deleteInventoryStatement == null)
                deleteInventoryStatement = conn.prepareStatement(deleteInventoryString);
            deleteInventoryStatement.setString(1, key.itemNumber);
            deleteInventoryStatement.setString(2, key.locationID);
            int rows = deleteInventoryStatement.executeUpdate();
            if (rows > 0)
                System.out.println("Deleted " + key + " from inventory table");
            else
                System.out.println("No record " + key + " to delete in inventory table");
        } catch (SQLException sqe) {
            sqe.printStackTrace();
        }
    }

    @Override
    public void deleteAll(Collection<InventoryKey> collection) {
        System.out.println("DeleteAll inventory with " + collection.size() + " items");
        for (InventoryKey key : collection)
            delete(key);
    }

    public void deleteAllItems() {
        try {
            if (deleteAllInventoryStatement == null)
                deleteAllInventoryStatement = conn.prepareStatement(deleteAllInventoryString);
            int d1 = deleteAllInventoryStatement.executeUpdate();
            System.out.println(d1 + " inventory records deleted from InventoryDB by InventoryMapStore");
            if (deleteAllLocationsStatement == null)
                deleteAllLocationsStatement = conn.prepareStatement(deleteAllLocationsString);
            int d2 = deleteAllLocationsStatement.executeUpdate();
            System.out.println(d2 + " location records deleted from InventoryDB by InventoryMapStore");
        } catch (SQLException sqe) {
            sqe.printStackTrace();
        }
    }

    //////////////////////////////
    // MapLoader implementation
    //////////////////////////////

    @Override
    public Inventory load(InventoryKey key) {
        // Note that writeBehindStore calls load
        //System.out.printf("load %s %s\n", key.itemNumber, key.locationID);
        return readInventory(key.itemNumber, key.locationID);
    }

    @Override
    public Map<InventoryKey, Inventory> loadAll(Collection<InventoryKey> collection) {
        //System.out.println("loadAll");
        Map<InventoryKey, Inventory> items = new HashMap<>();
        for (InventoryKey key : collection) {
            Inventory value = load(key);
            if (value != null)
                items.put(key, value);
        }
        //System.out.println("Loaded " + items.size() + " items from " + collection.size() + " keys");
        return items;
    }

    @Override
    public Iterable<InventoryKey> loadAllKeys() {
        //System.out.println("loadAllKeys");
        List<InventoryKey> keys = new ArrayList<>();
        try {
            if (selectItemKeysStatement == null) {
                selectItemKeysStatement = conn.prepareStatement(selectKeysString);
            }
            ResultSet rs = selectItemKeysStatement.executeQuery();
            if (rs == null) return null;
            System.out.println("Generating inventory keys for " + rs.getFetchSize() + " items");
            while (rs.next()) {
                InventoryKey key = new InventoryKey(rs.getString(1), rs.getString(2));
                keys.add(key);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Returning " + keys.size() + " keys from Inventory table");
        return keys;
    }
}
