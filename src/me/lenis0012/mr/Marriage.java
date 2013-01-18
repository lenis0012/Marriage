package me.lenis0012.mr;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.lenis0012.mr.children.ChildManager;
import me.lenis0012.mr.commands.MarryCMD;
import me.lenis0012.mr.listeners.PlayerListener;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Marriage extends JavaPlugin
{
	private List<String> partners = new ArrayList<String>();
	private FileConfiguration customConfig = null;
    private File customConfigFile = null;
    public List<String> chat =  new ArrayList<String>();
    public static Marriage instance;
    public HashMap<String, String> req = new HashMap<String, String>();
    public Economy economy;
    public Logger log = Logger.getLogger("Minecraft");
    public boolean eco = false;
	
	@Override
	public void onEnable()
	{
		FileConfiguration config = this.getConfig();
		PluginManager pm = this.getServer().getPluginManager();
		
		//register events/commands
		pm.registerEvents(new PlayerListener(this), this);
		getCommand("marry").setExecutor(new MarryCMD(this));
		
		//setup config.yml
		config.addDefault("settings.private-chat.format", "&a[Partner] &7{Player}&f: &a{Message}");
		config.addDefault("price.{command name}", 10.0);
		config.addDefault("price.marry", 0.0);
		config.options().copyDefaults(true);
		this.saveConfig();
		
		//setup data.yml
		FileConfiguration cfg = this.getCustomConfig();
		cfg.addDefault("partners", partners);
		cfg.options().copyDefaults(true);
		this.saveCustomConfig();
		
		//setup metrics
		try
		{
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch(Exception e)
		{
			this.getLogger().info("[Marriage] Failed sending stats to mcstats.org");
		}
		
		//setup instance
		instance  = (Marriage)pm.getPlugin("Marriage");
		ChildManager manager = new ChildManager(this);
		manager.setInstance(manager);
		//setup vault
		Plugin vault = pm.getPlugin("Vault");
		if(vault != null) {
			if(this.setupEconomy()) {
				String s = economy.getName();
				log.info("[Marriage] Hooked with "+s+" using Vault");
				eco = true;
			}
		}
	}
	
	public Player getPlayer(String name)
	{
		Player t = Bukkit.getServer().getPlayer(name);
		if(t != null)
			if(t.isOnline())
				return t;
		
		for(Player player : this.getServer().getOnlinePlayers())
		{
			if(player.getName().toLowerCase().startsWith(name) || player.getName().startsWith(name))
			{
				return player;
			}
		}
		
		for(Player player : this.getServer().getOnlinePlayers())
		{
			if(player.getName().toLowerCase().endsWith(name) || player.getName().endsWith(name))
			{
				return player;
			}
		}
		
		for(Player player : this.getServer().getOnlinePlayers())
		{
			if(player.getName().toLowerCase().contains(name) || player.getName().contains(name))
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
	
    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
}
