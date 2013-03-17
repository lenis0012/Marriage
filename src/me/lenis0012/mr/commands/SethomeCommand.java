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
		MPlayer mp = plugin.getMPlayer(player);
		if(!mp.isMarried())
		{
			player.sendMessage(ChatColor.RED + "You dont have a partner.");
			return;
		}
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
		mp.setHome(loc);
		
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
