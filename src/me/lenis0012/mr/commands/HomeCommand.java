package me.lenis0012.mr.commands;

import me.lenis0012.mr.MPlayer;
import me.lenis0012.mr.Marriage;
import me.lenis0012.mr.util.EcoUtil;

import org.bukkit.ChatColor;
import org.bukkit.Location;
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
		Location home = mp.getHome();
		if(!player.hasPermission("marry.home") && !player.hasPermission("marry.*"))
		{
			player.sendMessage(ChatColor.RED + "No permission.");
			return;
		}
		if(home == null)
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
		
		player.teleport(home);
		player.sendMessage(ChatColor.GREEN+"Teleporing to home...");
	}
}
