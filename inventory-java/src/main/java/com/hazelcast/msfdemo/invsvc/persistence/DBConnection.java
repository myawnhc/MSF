/*
 *  Copyright 2018-2021 Hazelcast, Inc
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.package com.theyawns.controller.launcher;
 */

package com.hazelcast.msfdemo.invsvc.persistence;

import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private final static ILogger log = Logger.getLogger(DBConnection.class);

    //protected Connection conn;
    //
    public static final String INVDB_USER="invuser";
    public static final String INVDB_PASS="invpass";

    public static synchronized Connection establishConnection()  {
        Connection conn = null;
        try {
            // Register the driver, we don't need to actually assign the class to anything
            Class.forName(InventoryDB.JDBC_DRIVER_CLASS);
            String jdbcURL = "jdbc:" + InventoryDB.JDBC_PROTOCOL + "://" +
                    InventoryDB.JDBC_HOST + ":" + InventoryDB.JDBC_PORT + "/" + InventoryDB.JDBC_DB_NAME;
            //log.info("Attempting connection to " + jdbcURL + " for user " + BankInABoxProperties.JDBC_USER);
            conn = DriverManager.getConnection(
                    jdbcURL, INVDB_USER, INVDB_PASS);
            log.info("Established connection to InventoryDB database");

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return conn;
    }
}
