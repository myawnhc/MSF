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

package org.hazelcast.msfdemo.invsvc.persistence;

import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class InventoryDB {
    private final static ILogger log = Logger.getLogger(InventoryDB.class);

    private Connection conn;

    private static final String createDatabaseString = "create database if not exists InventoryDB";
    private static final String dropDatabaseString   = "drop database if exists InventoryDB";
    private static final String createUserString = "create user if not exists 'invuser' identified with mysql_native_password by 'invpass'";
    private static final String grantUserString = "grant all on InventoryDB.* to 'invuser'@'%'";
    // Certain capabilities cannot be granted at the database level, must be *.*
    public static final String grantAdditional = "grant SHOW DATABASES, RELOAD, REPLICATION CLIENT, REPLICATION SLAVE on *.* to 'invuser'@'%'";

    //public static final String JDBC_DRIVER_CLASS="org.mariadb.jdbc.Driver";
    public static final String JDBC_DRIVER_CLASS="com.mysql.cj.jdbc.Driver";

    public static final String JDBC_PROTOCOL="mysql";
    public static final String JDBC_DB_NAME="InventoryDB";
    //public static final String JDBC_HOST="127.0.0.1";     //when running bare metal (laptop)
    public static final String JDBC_HOST="invdb";           // docker container name

    public static final String JDBC_PORT="3306";
    // Need username and password here that can be used BEFORE we create the db-specific user
    public static final String JDBC_USER="root";
    public static final String JDBC_PASS="secret";

    protected synchronized void establishConnection()  {
        try {
            // Register the driver, we don't need to actually assign the class to anything
            Class.forName(JDBC_DRIVER_CLASS);
            String jdbcURL = "jdbc:" + JDBC_PROTOCOL + "://" + JDBC_HOST + ":" + JDBC_PORT + "/";
            //System.out.println("JDBC URL is " + jdbcURL);
            conn = DriverManager.getConnection(
                    jdbcURL, JDBC_USER, JDBC_PASS);
            log.info("Established connection to MySQL/MariaDB server");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    protected synchronized void createDatabase() {
        if (conn == null) {
            throw new IllegalStateException("Must establish connection before creating the database!");
        }
        try (Statement stmt = conn.createStatement()) {
            //stmt.executeUpdate(dropDatabaseString);
            //log.info("Dropped (if exists) database InventoryDB");
            stmt.executeUpdate(createDatabaseString);
            log.info("Created Database InventoryDB");

        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public synchronized void createUser() {
        if (conn == null) {
            throw new IllegalStateException("Must establish connection before creating the database!");
        }
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createUserString);
            stmt.executeUpdate(grantUserString);
            stmt.executeUpdate(grantAdditional);

            log.info("Created user invuser");

        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    // For testing purposes
    public static void main(String[] args) {
        InventoryDB main = new InventoryDB();
        main.establishConnection();
        main.createDatabase();
        main.createUser();
    }
}
