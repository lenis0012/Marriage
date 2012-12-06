package me.lenis0012.mr.commands;

import java.util.WeakHashMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import me.lenis0012.mr.MPlayer;
import me.lenis0012.mr.Marriage;

public class MarryCommand
{
	public static WeakHashMap<String, String> req = new WeakHashMap<String, String>();
	
	public static void request(Player player, String[] args, Marriage plugin)
	{
		Player op = plugin.getPlayer(args[0]);
		if(op != null)
		{
			if(op.isOnline())
			{
				MPlayer mp = new MPlayer(op);
				MPlayer tp = new MPlayer(player);
				if(!player.hasPermission("marry.marry") && !player.hasPermission("marry.*"))
				{
					player.sendMessage(ChatColor.RED + "No permission.");
					return;
				}
				if(args[0].equals(player.getName()))
				{
					player.sendMessage(ChatColor.RED + "You may not marry yourself!");
					return;
				}
				if(mp.isMarried())
				{
					player.sendMessage(ChatColor.RED + "You are already married.");
					return;
				}
				if(tp.isMarried())
				{
					player.sendMessage(ChatColor.RED + op.getName() + " is already married.");
					return;
				}
				
				player.sendMessage(ChatColor.GREEN + "Request sended.");
				op.sendMessage(ChatColor.GREEN + player.getName() + " would like to marry with you.");
				op.sendMessage(ChatColor.GREEN + "Type " + ChatColor.LIGHT_PURPLE + "/marry accept "
				+ ChatColor.GREEN + "to accept");
				req.put(op.getName(), player.getName());
				return;
			}
		}
		player.sendMessage(ChatColor.RED+"Invalid player");
	}
}
