package com.teampingui.dao;

import com.teampingui.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDateTime;

public class Database {
    /**
     * Location of database
     */
    public static final String LOCATION = Main.class.getResource("/database/database.db").toExternalForm();
    /**
     * Required Table for Programm to work
     */
    private static final String REQUIRED_TABLE = "habit";
    //Initializing the logger
    private static final Logger log = LogManager.getLogger(Database.class);

    public static boolean isOK() {
        if (!checkDrivers()) return false;
        if (!checkConnection()) return false;
        return checkTables();
    }

    /**
     * @return when true -> Program is able to start. When false -> Program won't start and return an error message
     */
    private static boolean checkDrivers() {
        try {
            Class.forName("org.sqlite.JDBC");
            DriverManager.registerDriver(new org.sqlite.JDBC());
            log.info("Successfully started SQLite Drivers.");
            return true;
        } catch (ClassNotFoundException | SQLException classNotFoundException) {
            log.error(LocalDateTime.now() + ": Could not start SQLite Drivers." + classNotFoundException.getMessage());
            return false;
        }
    }

    private static boolean checkConnection() {
        try (Connection connection = connect()) {
            log.info("Successfully connected to database.");
            return connection != null;
        } catch (SQLException e) {
            log.error(LocalDateTime.now() + ": Could not connect to database. " + e.getMessage());
            return false;
        }
    }

    private static boolean checkTables() {
        String checkTables = "select DISTINCT tbl_name from sqlite_master where tbl_name = '" + REQUIRED_TABLE + "'";

        try (Connection connection = Database.connect()) {
            PreparedStatement statement = connection.prepareStatement(checkTables);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                if (rs.getString("tbl_name").equals(REQUIRED_TABLE)) return true;
            }
            log.info("Successfully checked tables in database.");
        } catch (SQLException exception) {
            log.error("Could not find tables in database. " + exception.getMessage());
            return false;
        }
        return false;
    }

    /**
     * @return Connection Object to connect to the Database
     */
    protected static Connection connect() {
        String dbPrefix = "jdbc:sqlite:";
        Connection connection;
        try {
            connection = DriverManager.getConnection(dbPrefix + LOCATION);
            log.info("Successfully connected to SQLite DB at " + LOCATION + ".");
        } catch (SQLException exception) {
            log.error("Could not connect to SQLite DB at " + LOCATION + ". " + exception.getMessage());
            return null;
        }
        return connection;
    }

}
