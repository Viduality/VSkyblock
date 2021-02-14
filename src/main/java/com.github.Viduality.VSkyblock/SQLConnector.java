package com.github.Viduality.VSkyblock;

/*
 * VSkyblock
 * Copyright (C) 2020  Viduality
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLConnector {

    private static final VSkyblock plugin = VSkyblock.getInstance();
    public static final HikariConfig config = new HikariConfig();
    public static HikariDataSource ds;


    /**
     * Database user username
     * @return Database user username
     */
    public String getDbUser() {
        return ConfigShorts.getDefConfig().getString("database.user");
    }

    /**
     * Database password
     * @return Database password
     */
    public String getDbPassword() {
        return ConfigShorts.getDefConfig().getString("database.password");
    }

    /**
     * Get Database name
     * @return Database name
     */
    public String getDatabase() {
        return ConfigShorts.getDefConfig().getString("database.database");
    }

    /**
     * Get Database URL
     * @return Database URL
     */
    public String getDbUrl() {
        return ConfigShorts.getDefConfig().getString("database.url");
    }

    /**
     * Get Database URL parameters
     * @return Database URL parameters
     */
    public String getDbUrlParameters() {
        return ConfigShorts.getDefConfig().getString("database.url-parameters");
    }

    /**
     * Initiates connection.
     */
    public void initConnection() {
        if (getDatabase() != null && !getDatabase().isEmpty()) {
            initTables();
        }
    }

    {
        config.setJdbcUrl("jdbc:mysql://"
                + getDbUrl() + "/"
                + getDatabase());
        config.setUsername(getDbUser());
        config.setPassword(getDbPassword());
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);
    }

    /**
     * Returns the connection to the database.
     * Used to access the database.
     * @return Connection to the database
     */
    public Connection getConnection() {
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Initiate tables
     * Initiates tables and creates them if they don't exist
     */
    public void initTables() {
        try {
            Connection connection = getConnection();
            connection.createStatement().execute(
                    "CREATE DATABASE IF NOT EXISTS " + getDatabase());
            connection.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS VSkyblock_Player("
                            + "playername VARCHAR(16) NOT NULL,"
                            + "uuid CHAR(64) UNIQUE NOT NULL,"
                            + "islandid BIGINT,"
                            + "islandowner BOOLEAN NOT NULL DEFAULT false,"
                            + "owneruuid CHAR(32),"
                            + "kicked BOOLEAN NOT NULL DEFAULT false,"
                            + "deaths BIGINT DEFAULT 0,"
                            + "lastX DOUBLE,"
                            + "lastY DOUBLE,"
                            + "lastZ DOUBLE,"
                            + "lastPitch DOUBLE,"
                            + "lastYaw DOUBLE,"
                            + "lastWorld CHAR(128),"
                            + "highestreachedlevel VARCHAR(100) DEFAULT 0,"
                            + "PRIMARY KEY (playername));");
            connection.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS VSkyblock_Island("
                            + "islandid BIGINT AUTO_INCREMENT NOT NULL,"
                            + "island VARCHAR(100) NOT NULL,"
                            + "islandlevel VARCHAR(100) DEFAULT 0,"
                            + "difficulty VARCHAR(100) NOT NULL DEFAULT 'NORMAL',"
                            + "visit BOOLEAN NOT NULL DEFAULT TRUE,"
                            + "visitneedsrequest BOOLEAN NOT NULL DEFAULT FALSE,"
                            + "cobblestonelevel BIGINT DEFAULT 0,"
                            + "totalblocks BIGINT DEFAULT 140,"
                            + "PRIMARY KEY (islandid))");
            connection.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS VSkyblock_IslandLocations("
                            + "islandid BIGINT NOT NULL,"
                            + "spawnX DOUBLE NOT NULL DEFAULT 0,"
                            + "spawnY DOUBLE NOT NULL DEFAULT 67,"
                            + "spawnZ DOUBLE NOT NULL DEFAULT 0,"
                            + "spawnYaw FLOAT NOT NULL DEFAULT 0,"
                            + "spawnPitch FLOAT NOT NULL DEFAULT 0,"
                            + "netherX DOUBLE,"
                            + "netherY DOUBLE,"
                            + "netherZ DOUBLE,"
                            + "netherYaw FLOAT,"
                            + "netherWorld CHAR(128),"
                            + "PRIMARY KEY (islandid));");
            connection.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS VSkyblock_Challenges("
                            + "islandid BIGINT NOT NULL,"
                            + "challenge VARCHAR(100) NOT NULL,"
                            + "count BIGINT DEFAULT 0,"
                            + "tracked BOOLEAN DEFAULT false,"
                            + "INDEX (islandid), UNIQUE (challenge, islandid));");
            //Auto adds new columns (if they are implemented in future updates and the plugin is already running on the server)
            connection.createStatement().execute(
                    "ALTER TABLE VSkyblock_Player ADD COLUMN IF NOT EXISTS("
                    + "deaths BIGINT DEFAULT 0,"
                    + "lastX DOUBLE,"
                    + "lastY DOUBLE,"
                    + "lastZ DOUBLE,"
                    + "lastPitch DOUBLE,"
                    + "lastYaw DOUBLE,"
                    + "lastWorld CHAR(128),"
                    + "highestreachedlevel VARCHAR(100) DEFAULT 0);"
            );
            connection.createStatement().execute(
                    "ALTER TABLE VSkyblock_Island ADD COLUMN IF NOT EXISTS("
                    + "visitneedsrequest BOOLEAN NOT NULL DEFAULT FALSE,"
                    + "totalblocks BIGINT DEFAULT 140);"
            );
            connection.createStatement().execute(
                    "ALTER TABLE VSkyblock_IslandLocations ADD COLUMN IF NOT EXISTS("
                    + "spawnX DOUBLE,"
                    + "spawnY DOUBLE,"
                    + "spawnZ DOUBLE,"
                    + "spawnYaw FLOAT,"
                    + "spawnPitch FLOAT);"
            );
            connection.createStatement().execute(
                    "ALTER TABLE VSkyblock_Challenges ADD COLUMN IF NOT EXISTS("
                    + "tracked BOOLEAN DEFAULT false);"
            );
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Should never be used since it does nothing special ;)
     */
    public void close(){
        ds.close();
    }
}
