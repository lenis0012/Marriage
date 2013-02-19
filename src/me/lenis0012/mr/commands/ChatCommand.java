package me.lenis0012.mr.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.lenis0012.mr.MPlayer;
import me.lenis0012.mr.Marriage;
import me.lenis0012.mr.util.EcoUtil;

public class ChatCommand
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
		Player op = Bukkit.getServer().getPlayer(mp.getPartner());
		if(op == null)
		{
			player.sendMessage(ChatColor.RED + "Your partner is not online");
			return;
		}
		if(!op.isOnline())
		{
			player.sendMessage(ChatColor.RED + "Your partner is not online");
			return;
		}
		if(!player.hasPermission("marry.chat") && !player.hasPermission("marry.*"))
		{
			player.sendMessage(ChatColor.RED + "No permission.");
			return;
		}
		if(plugin.eco) {
			double a = EcoUtil.getPriceFromConfig("chat");
			if(a != 0.0) {
				if(EcoUtil.withrawMoneyIfEnough(player, a)) {
					return;
				}
			}
		}
		
		if(mp.isChatting())
		{
			mp.setChatting(false);
			player.sendMessage(ChatColor.RED+"Left partner chat");
		}
		else
		{
			mp.setChatting(true);
			player.sendMessage(ChatColor.GREEN+"Joined partner chat");
		}
	}
}
