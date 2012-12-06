package me.lenis0012.mr;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import me.lenis0012.mr.commands.MarryCMD;
import me.lenis0012.mr.listeners.ChatListener;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Marriage extends JavaPlugin
{
	private List<String> partners = new ArrayList<String>();
	private FileConfiguration customConfig = null;
    private File customConfigFile = null;
    public List<String> chat =  new ArrayList<String>();
	
	@Override
	public void onEnable()
	{
		FileConfiguration config = this.getConfig();
		PluginManager pm = this.getServer().getPluginManager();
		
		pm.registerEvents(new ChatListener(this), this);
		getCommand("marry").setExecutor(new MarryCMD(this));
		
		config.addDefault("settings.private-chat.format", "&a[Partner] &7{Player}&f: &a{Message}");
		config.options().copyDefaults(true);
		this.saveConfig();
		
		FileConfiguration cfg = this.getCustomConfig();
		cfg.addDefault("partners", partners);
		cfg.options().copyDefaults(true);
		this.saveCustomConfig();
		
		try
		{
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch(Exception e)
		{
			this.getLogger().info("[Marriage] Failed sending stats to mcstats.org");
		}
	}
	
	public Player getPlayer(String name)
	{
		for(Player player : this.getServer().getOnlinePlayers())
		{
			if(player.getName().toLowerCase().startsWith(name))
			{
				return player;
			}
		}
		
		for(Player player : this.getServer().getOnlinePlayers())
		{
			if(player.getName().toLowerCase().contains(name))
			{
				return player;
			}
		}
		
		return null;
	}
	
	public void reloadCustomConfig()
	{
	    if (customConfigFile == null)
	    {
	    	customConfigFile = new File(getDataFolder(), "data.yml");
	    }
	    customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
	    java.io.InputStream defConfigStream = this.getResource("data.yml");
	    if (defConfigStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        customConfig.setDefaults(defConfig);
	    }
	}

	public FileConfiguration getCustomConfig()
	{
	    if (customConfig == null)
	    {
	        this.reloadCustomConfig();
	    }
	    return customConfig;
	}

	public void saveCustomConfig()
	{
	    if (customConfig == null || customConfigFile == null)
	    {
	    	return;
	    }
	    try {
	        getCustomConfig().save(customConfigFile);
	    } catch (IOException ex) {
	        this.getLogger().log(Level.SEVERE, "Could not save config to " + customConfigFile, ex);}
	}
	
	public String fixColors(String message)
	{
		message = message.replaceAll("&0", ChatColor.BLACK.toString());
		message = message.replaceAll("&1", ChatColor.DARK_BLUE.toString());
		message = message.replaceAll("&2", ChatColor.DARK_GREEN.toString());
		message = message.replaceAll("&3", ChatColor.DARK_AQUA.toString());
		message = message.replaceAll("&4", ChatColor.DARK_RED.toString());
		message = message.replaceAll("&5", ChatColor.DARK_PURPLE.toString());
		message = message.replaceAll("&6", ChatColor.GOLD.toString());
		message = message.replaceAll("&7", ChatColor.GRAY.toString());
		message = message.replaceAll("&8", ChatColor.DARK_GRAY.toString());
		message = message.replaceAll("&9", ChatColor.BLUE.toString());
		message = message.replaceAll("&a", ChatColor.GREEN.toString());
		message = message.replaceAll("&b", ChatColor.AQUA.toString());
		message = message.replaceAll("&c", ChatColor.RED.toString());
		message = message.replaceAll("&d", ChatColor.LIGHT_PURPLE.toString());
		message = message.replaceAll("&e", ChatColor.YELLOW.toString());
		message = message.replaceAll("&f", ChatColor.WHITE.toString());
		message = message.replaceAll("&r", ChatColor.WHITE.toString());
		return message;
	}
}
