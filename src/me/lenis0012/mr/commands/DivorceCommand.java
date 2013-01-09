package me.lenis0012.mr.commands;

import java.util.List;

import me.lenis0012.mr.MPlayer;
import me.lenis0012.mr.Marriage;
import me.lenis0012.mr.util.EcoUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DivorceCommand
{
	public static void perfrom(Player player)
	{
		Marriage plugin = Marriage.instance;
		MPlayer mp = new MPlayer(player);
		if(!mp.isMarried())
		{
			player.sendMessage(ChatColor.RED+"You are not married");
			return;
		}
		if(plugin.eco) {
			double a = EcoUtil.getPriceFromConfig("divorce");
			if(a != 0.0) {
				if(EcoUtil.withrawMoneyIfEnough(player, a)) {
					return;
				}
			}
		}
		
		String user = player.getName();
		String partner = mp.getPartner();
		
		plugin.getCustomConfig().set("Married."+user, null);
		plugin.getCustomConfig().set("Married."+partner, null);
		plugin.getCustomConfig().set("home."+user, null);
		plugin.getCustomConfig().set("home."+partner, null);
		List<String> list = plugin.getCustomConfig().getStringList("partners");
		if(list.contains(user))
			list.remove(user);
		if(list.contains(partner))
			list.remove(partner);
		plugin.getCustomConfig().set("partners", list);
		plugin.saveCustomConfig();
		Bukkit.getServer().broadcastMessage(ChatColor.RED+user+" divorced with "+partner);
	}
}
