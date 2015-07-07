package com.lenis0012.bukkit.marriage2.internal.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lenis0012.bukkit.marriage2.Gender;
import com.lenis0012.bukkit.marriage2.MData;
import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.internal.MarriageCore;
import com.lenis0012.bukkit.marriage2.misc.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class DataConverter {
    private final MarriageCore core;
    private File dir;
    private int totalFiles;
    private int completed;
    private int lastPercent;

    public DataConverter(MarriageCore core) {
        this.core = core;
    }

    public boolean isOutdated() {
        this.dir = new File(core.getPlugin().getDataFolder(), "playerdata");
        return dir.exists();
    }

    public void convert() {
        String[] files = dir.list();
        this.totalFiles = files.length;
        core.getLogger().log(Level.INFO, "Converting " + totalFiles + " old database entries...");
        core.getLogger().log(Level.INFO, "Retrieving UUIDs...");

        // Retrieve UUIDs from mojang
        Map<String, UUID> uuidMap = Maps.newHashMap();
        UUIDFetcher uuidFetcher = new UUIDFetcher(new ArrayList<String>());
        for(completed = 0; completed < totalFiles; completed++) {
            String name = files[completed].replace(".yml", "");
            uuidFetcher.addName(name);
            if(uuidFetcher.size() >= 100 || completed >= totalFiles - 1) {
                try {
                    uuidMap.putAll(uuidFetcher.call());
                } catch(Exception e) {
                    core.getLogger().log(Level.WARNING, "Failed to retrieve UUID for 100 players!");
                }
            }

            int percent = (completed + 1) / totalFiles;
            if(percent >= lastPercent + 5) {
                lastPercent += 5;
                reportStatus(percent);
            }
        }

        // Insert data into new DB...
        core.getLogger().log(Level.INFO, "Inserting user data into new database...");
        this.completed = 0;
        for(Map.Entry<String, UUID> entry : uuidMap.entrySet()) {
            try {
                String name = entry.getKey();
                File file = new File(dir, name + ".yml");
                FileConfiguration cnf = YamlConfiguration.loadConfiguration(file);
                cnf.load(file);

                MPlayer mp = core.getMPlayer(entry.getValue());
                if(cnf.contains("partner")) {
                    UUID uuid = uuidMap.get(cnf.getString("partner"));
                    if(uuid != null) {
                        MPlayer mp2 = core.getMPlayer(uuid);
                        MData mdata = core.marry(mp, mp2);

                        if(cnf.contains("home")) {
                            World world = Bukkit.getWorld(cnf.getString("home.world"));
                            if(world != null) {
                                double x = cnf.getDouble("home.x", 0.0);
                                double y = cnf.getDouble("home.y", 0.0);
                                double z = cnf.getDouble("home.z", 0.0);
                                float yaw = (float) cnf.getDouble("home.yaw", 0.0);
                                float pitch = (float) cnf.getDouble("home.pitch", 0.0);
                                Location location = new Location(world, x, y, z, yaw, pitch);
                                mdata.setHome(location);
                            }
                        }
                    }
                }
            } catch(Exception e) {
                core.getLogger().log(Level.WARNING, "Failed to convert data for player!", e);
            }

            int percent = ++completed / totalFiles;
            reportStatus(percent);
        }

        // Save changes
        core.getLogger().log(Level.INFO, "Saving changes in database...");
        core.unloadAll();

        // Reset old data
        core.getLogger().log(Level.INFO, "Renaming playerdata file...");
        while(!dir.renameTo(new File(core.getPlugin().getDataFolder(), "playerdata_backup"))) {
            try {
                Thread.sleep(50L);
            } catch(InterruptedException e) {
            }
        }
    }

    private void reportStatus(int percent) {
        StringBuilder bar = new StringBuilder("[");
        for(int i = 0; i < percent; i+= 5) {
            bar.append('=');
        }
        for(int i = percent; i < 100; i+= 5) {
            bar.append('_');
        }
        bar.append("] (").append(percent).append("%)");
    }
}
