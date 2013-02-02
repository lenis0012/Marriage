package me.lenis0012.mr.commands;

import me.lenis0012.mr.MPlayer;
import me.lenis0012.mr.Marriage;
import me.lenis0012.mr.util.EcoUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class HomeCommand
{
	public static void perform(Player player)
	{
		Marriage plugin = Marriage.instance;
		MPlayer mp = plugin.getMPlayer(player);
		if(!mp.isMarried())
		{
			player.sendMessage(ChatColor.RED + "You dont have a partner.");
			return;
		}
		String user = player.getName();
		if(!player.hasPermission("marry.home") && !player.hasPermission("marry.*"))
		{
			player.sendMessage(ChatColor.RED + "No permission.");
			return;
		}
		if(plugin.getCustomConfig().getString("home."+user+".world") == null)
		{
			player.sendMessage(ChatColor.RED + "Home not set");
			return;
		}
		if(plugin.eco) {
			double a = EcoUtil.getPriceFromConfig("home");
			if(a != 0.0) {
				if(EcoUtil.withrawMoneyIfEnough(player, a)) {
					return;
				}
			}
		}
		
		World world = Bukkit.getServer().getWorld(plugin.getCustomConfig().getString("home."+user+".world"));
		int x = plugin.getCustomConfig().getInt("home."+user+".x");
		int y = plugin.getCustomConfig().getInt("home."+user+".y");
		int z = plugin.getCustomConfig().getInt("home."+user+".z");
		float yaw = Float.valueOf(plugin.getCustomConfig().getString("home."+user+".yaw"));
		float pitch = Float.valueOf(plugin.getCustomConfig().getString("home."+user+".pitch"));
		Location home = new Location(world, x, y, z, yaw, pitch);
		player.teleport(home);
		player.sendMessage(ChatColor.GREEN+"Teleporing to home...");
	}
}
