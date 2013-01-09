package me.lenis0012.mr.commands;

import me.lenis0012.mr.MPlayer;
import me.lenis0012.mr.Marriage;
import me.lenis0012.mr.util.EcoUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SethomeCommand
{
	public static void perform(Player player)
	{
		Marriage plugin = Marriage.instance;
		MPlayer mp = new MPlayer(player);
		if(!mp.isMarried())
		{
			player.sendMessage(ChatColor.RED + "You dont have a partner.");
			return;
		}
		String user = player.getName();
		String partner = mp.getPartner();
		if(!player.hasPermission("marry.sethome") && !player.hasPermission("marry.*"))
		{
			player.sendMessage(ChatColor.RED + "No permission.");
			return;
		}
		if(plugin.eco) {
			double a = EcoUtil.getPriceFromConfig("sethome");
			if(a != 0.0) {
				if(EcoUtil.withrawMoneyIfEnough(player, a)) {
					return;
				}
			}
		}
		
		Location loc = player.getLocation();
		int x = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();
		String yaw = String.valueOf(loc.getYaw());
		String pitch = String.valueOf(loc.getPitch());
		String world = loc.getWorld().getName();
		plugin.getCustomConfig().set("home."+user+".world", world);
		plugin.getCustomConfig().set("home."+user+".x", x);
		plugin.getCustomConfig().set("home."+user+".y", y);
		plugin.getCustomConfig().set("home."+user+".z", z);
		plugin.getCustomConfig().set("home."+user+".yaw", yaw);
		plugin.getCustomConfig().set("home."+user+".pitch", pitch);
		plugin.getCustomConfig().set("home."+partner+".world", world);
		plugin.getCustomConfig().set("home."+partner+".x", x);
		plugin.getCustomConfig().set("home."+partner+".y", y);
		plugin.getCustomConfig().set("home."+partner+".z", z);
		plugin.getCustomConfig().set("home."+partner+".yaw", yaw);
		plugin.getCustomConfig().set("home."+partner+".pitch", pitch);
		plugin.saveCustomConfig();
		
		player.sendMessage(ChatColor.GREEN+"Home set");
		Player op = Bukkit.getPlayer(partner);
		if(op != null)
		{
			if(op.isOnline())
			{
				op.sendMessage(ChatColor.GREEN+"Your partner has set your home");
			}
		}
	}
}
