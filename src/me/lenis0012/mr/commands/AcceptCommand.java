package me.lenis0012.mr.commands;

import me.lenis0012.mr.MPlayer;
import me.lenis0012.mr.Marriage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AcceptCommand
{
	public static void Accept(Player player)
	{
		Marriage plugin = Marriage.instance;
		
		if(plugin.req.containsKey(player.getName()))
		{
			Player op = Bukkit.getServer().getPlayer(plugin.req.get(player.getName()));
			if(op != null)
			{
				if(op.isOnline())
				{
					String user = op.getName();
					String name = player.getName();
					MPlayer mp = plugin.getMPlayer(player);
					mp.setPartner(user);
					
					Bukkit.getServer().broadcastMessage(ChatColor.GREEN + user + " has married with " + name);
					plugin.req.remove(name);
					return;
				}
				player.sendMessage(ChatColor.RED + "Player that requested you is not online");
				return;
			}
		}
		player.sendMessage(ChatColor.RED + "You dont got a request!");
	}
}
