package me.lenis0012.mr;

import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class MPlayer
{
	private String name = null;
	private FileConfiguration cfg;
	private File file = null;
	
	public MPlayer(Player player)
	{
		this.name = player.getName();
		this.file = new File("plugins/Marriage/data.yml");
		this.cfg = YamlConfiguration.loadConfiguration(file);
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
}
