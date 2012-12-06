package me.lenis0012.mr.commands;

import me.lenis0012.mr.MPlayer;
import me.lenis0012.mr.Marriage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TpCommand
{
	public static void perfrom(Player player, Marriage plugin)
	{
		MPlayer mp = new MPlayer(player);
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
		if(!player.hasPermission("marry.tp") && !player.hasPermission("marry.*"))
		{
			player.sendMessage(ChatColor.RED + "No permission.");
			return;
		}
		
		player.sendMessage(ChatColor.GREEN + "Teleporting...");
		op.sendMessage(ChatColor.GREEN + "Partner teleporting to you...");
		player.teleport(op.getLocation());
	}
}
