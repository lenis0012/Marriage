package com.lenis0012.bukkit.marriage2.commands;

import com.lenis0012.bukkit.marriage2.config.Permissions;
import com.lenis0012.bukkit.marriage2.internal.MarriageCore;
import com.lenis0012.bukkit.marriage2.internal.MarriagePlugin;
import com.lenis0012.bukkit.marriage2.internal.data.DataManager;
import com.lenis0012.bukkit.marriage2.internal.data.Driver;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandMigrate extends Command {
    public CommandMigrate(MarriageCore marriage) {
        super(marriage, "migrate");
        setMinArgs(2);
        setHidden(true);
        setPermission(Permissions.ADMIN);
    }

    @Override
    public void execute() {
        Driver driver;
        final DataManager newDatabase = marriage.getDataManager();
        if(getArg(0).equalsIgnoreCase("sqlite") && getArg(1).equalsIgnoreCase("mysql")) {
            driver = Driver.SQLITE;
        } else if(getArg(0).equalsIgnoreCase("mysql") && getArg(1).equalsIgnoreCase("sqlite")) {
            driver = Driver.MYSQL;
        } else {
            reply("&cUsage: /marry migrate <old db> <new db>");
            return;
        }

        final boolean fastMode = getArgLength() <= 2 || !getArg(2).equalsIgnoreCase("false");
        final DataManager oldDatabase = new DataManager(JavaPlugin.getPlugin(MarriagePlugin.class), driver);

        reply("&aStarting migration process (might take a while)");
        Bukkit.getScheduler().runTaskAsynchronously(JavaPlugin.getPlugin(MarriagePlugin.class), () -> {
            boolean success = oldDatabase.migrateTo(newDatabase, !fastMode);
            oldDatabase.close(); // Disconnect from old db
            reply(success ? "&aSuccessfully migrated database!" : "&cSomething went wrong while migrating, check log.");
        });
    }
}
