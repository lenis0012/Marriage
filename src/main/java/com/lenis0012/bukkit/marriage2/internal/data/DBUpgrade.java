package com.lenis0012.bukkit.marriage2.internal.data;

import java.sql.SQLException;
import java.sql.Statement;

public class DBUpgrade {
    private static final int VERSION_ID = 2;

    public int getVersionId() {
        return VERSION_ID;
    }

    public void run(Statement statement, int currentVersion, String prefix) throws SQLException {
        switch(currentVersion) {
            case 1:
                statement.execute("ALTER TABLE " + prefix + "players ADD last_name VARCHAR(16);");
                break;
            default:
                break;
        }
    }
}
