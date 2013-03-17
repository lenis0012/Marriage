package me.lenis0012.mr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class PlayerConfig extends YamlConfiguration {
	private File file;
	
	public PlayerConfig(File file) {
		this.file = file;
		this.reload();
	}
	
	public void save() {
		try {
			this.save(this.file);
		} catch (IOException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot save " + file, ex);
		}
	}
	
	public void reload() {
		try {
			this.load(file);
		} catch (FileNotFoundException ex) {
		} catch (IOException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
		} catch (InvalidConfigurationException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
		}
	}
}