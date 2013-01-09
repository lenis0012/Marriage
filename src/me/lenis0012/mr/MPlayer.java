package me.lenis0012.mr;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class MPlayer
{
	private String name = null;
	private FileConfiguration cfg;
	private Marriage plugin;
	
	public MPlayer(Player player)
	{
		plugin = Marriage.instance;
		this.name = player.getName();
		this.cfg = plugin.getCustomConfig();
	}
	
	public boolean isMarried()
	{
		String par1Str = cfg.getString("Married." + name);
		return par1Str != null && par1Str != "";
	}
	
	public String getPartner()
	{
		if(isMarried())
		{
			String par1Str = cfg.getString("Married." + name);
			return par1Str;
		}
		return "";
	}
	
	public void setPartner(String user) {
		cfg.set("Married." + name, user);
		cfg.set("Married." + user, name);
		save();
	}
	
	private void save() {
		plugin.saveCustomConfig();
	}
}
