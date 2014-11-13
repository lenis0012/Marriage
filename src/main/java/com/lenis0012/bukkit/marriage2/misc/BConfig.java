package com.lenis0012.bukkit.marriage2.misc;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.configuration.file.YamlConfiguration;

import com.lenis0012.bukkit.marriage2.internal.MarriageBase;

/**
 * Represents a more easy version of FileConfiguration
 * Allows saving and reloading without throwing exceptions or needing the specify a file.
 */
public class BConfig extends YamlConfiguration {
	private final MarriageBase core;
	private final File file;
	
	public BConfig(MarriageBase core, File file) {
		this.core = core;
		this.file = file;
		file.getParentFile().mkdirs();
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				;
			}
		}
		
		reload();
	}
	
	public void reload() {
		try {
			load(file);
		} catch (Exception e) {
			core.getLogger().log(Level.WARNING, "Failed to reload configuration file", e);
		}
	}
	
	public void save() {
		try {
			save(file);
		} catch (Exception e) {
			core.getLogger().log(Level.WARNING, "Failed to save configuration file", e);
		}
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
}
