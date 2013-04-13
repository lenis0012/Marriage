package me.lenis0012.mr.lang;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;


import me.lenis0012.mr.Marriage;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;


public class LangConfig extends YamlConfiguration {
	private static LangConfig instance;
	
	public static LangConfig get() {
		if(instance != null)
			return instance;
		else
			return instance = new LangConfig();
	}
	
	private Marriage plugin;
	private File file;
	
	public LangConfig() {
		this.plugin = Marriage.instance;
		this.file = new File(plugin.getDataFolder(), "lang.yml");
		plugin.getDataFolder().mkdir();
		
		try {
			if(!file.exists())
				file.createNewFile();
		} catch(IOException e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to create lang.yml", e);
		}
		
		this.reload();
	}
	
	public void reload() {
		try {
			this.load(file);
		} catch (FileNotFoundException e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to load lang.yml (not found)", e);
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to load lang.yml", e);
		} catch (InvalidConfigurationException e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to load lang.yml (invalid)", e);
		}
	}
	
	public void save() {
		try {
			this.save(file);
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to save lang.yml", e);
		}
	}
	
	public String getMessageWithDefault(String path, String def) {
		if(this.contains("messages." + path))
			return this.getString("messages." + path);
		else {
			this.set("messages." + path, def);
			this.save();
			return def;
		}
	}
	
	public String getWordWithDefault(String path, String def) {
		if(this.contains("words." + path))
			return this.getString("words." + path);
		else {
			this.set("words." + path, def);
			this.save();
			return def;
		}
	}
}