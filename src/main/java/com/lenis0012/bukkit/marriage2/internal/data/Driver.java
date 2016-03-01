package com.lenis0012.bukkit.marriage2.internal.data;

import java.sql.SQLException;
import java.sql.Statement;

public enum Driver {
    MYSQL("com.mysql.jdbc.Driver"),
    SQLITE("org.sqlite.JDBC");

    private final String className;

    Driver(String className) {
        this.className = className;
    }

    public void initiate() throws Exception {
        Class.forName(className);
    }

    public void runSetup(Statement statement, String prefix) throws SQLException {
        statement.executeUpdate(String.format("CREATE TABLE IF NOT EXISTS %splayers ("
                + "unique_user_id VARCHAR(128) NOT NULL UNIQUE,"
                + "last_name VARCHAR(16),"
                + "gender VARCHAR(32),"
                + "priest BIT,"
                + "lastlogin BIGINT);", prefix));

        switch(this) {
            case MYSQL:
                statement.executeUpdate(String.format("CREATE TABLE IF NOT EXISTS %smarriages ("
                        + "id INT NOT NULL AUTO_INCREMENT,"
                        + "player1 VARCHAR(128) NOT NULL,"
                        + "player2 VARCHAR(128) NOT NULL,"
                        + "home_world VARCHAR(128) NOT NULL,"
                        + "home_x DOUBLE,"
                        + "home_y DOUBLE,"
                        + "home_z DOUBLE,"
                        + "home_yaw FLOAT,"
                        + "home_pitch FLOAT,"
                        + "pvp_enabled BIT,"
                        + "PRIMARY KEY(id));", prefix));
                break;
            case SQLITE:
                statement.executeUpdate(String.format("CREATE TABLE IF NOT EXISTS %smarriages ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "player1 VARCHAR(128) NOT NULL,"
                        + "player2 VARCHAR(128) NOT NULL,"
                        + "home_world VARCHAR(128) NOT NULL,"
                        + "home_x DOUBLE,"
                        + "home_y DOUBLE,"
                        + "home_z DOUBLE,"
                        + "home_yaw FLOAT,"
                        + "home_pitch FLOAT,"
                        + "pvp_enabled BIT);", prefix));
        }

        statement.executeUpdate(String.format("CREATE TABLE IF NOT EXISTS %sversion ("
                + "version_id INT);", prefix));
    }
}
