package com.lenis0012.bukkit.marriage2.misc;

import com.lenis0012.bukkit.marriage2.internal.MarriagePlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Level;

/**
 * Represents a more easy version of FileConfiguration
 * Allows saving and reloading without throwing exceptions or needing the specify a file.
 */
public class BConfig extends YamlConfiguration {
    private final MarriagePlugin plugin;
    private final File file;

    public BConfig(MarriagePlugin plugin, File file) {
        this.plugin = plugin;
        this.file = file;
        file.getParentFile().mkdirs();
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch(IOException e) {
                ;
            }
        }

        reload();
    }

    public void reload() {
        try {
            load(file);
        } catch(Exception e) {
            plugin.getLogger().log(Level.WARNING, "Failed to reload configuration file", e);
        }
    }

    public void save() {
        try {
            save(file);
        } catch(Exception e) {
            plugin.getLogger().log(Level.WARNING, "Failed to save configuration file", e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getOrDefault(String key, T def) {
        return contains(key) ? (T) get(key) : def;
    }

    @SuppressWarnings("unchecked")
    public <T> T getOrSet(String key, T def) {
        if(contains(key)) {
            return (T) get(key);
        } else {
            set(key, def);
            return def;
        }
    }

    public <T> T get(String key, Class<T> type) {
        return type.cast(get(key));
    }

    public static void copyFile(InputStream input, File file) {
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while((length = input.read(buffer, 0, buffer.length)) != -1) {
                output.write(buffer, 0, length);
            }
        } catch(Exception e) {
            JavaPlugin.getPlugin(MarriagePlugin.class).getLogger().log(Level.WARNING, "Failed to copy file", e);
        } finally {
            if(input != null) {
                try {
                    input.close();
                } catch(IOException e1) {
                }
            }
            if(output != null) {
                try {
                    output.close();
                } catch(IOException e1) {
                }
            }
        }
    }
}
