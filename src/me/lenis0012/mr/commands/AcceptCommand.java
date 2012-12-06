package me.lenis0012.mr.commands;

import java.util.List;
import me.lenis0012.mr.Marriage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class AcceptCommand
{
	public static void Accept(Player player, Marriage plugin)
	{
		FileConfiguration cfg = plugin.getCustomConfig();
		
		if(MarryCommand.req.containsKey(player.getName()))
		{
			Player op = plugin.getPlayer(MarryCommand.req.get(player.getName()));
			if(op != null)
			{
				if(op.isOnline())
				{
					String user = op.getName();
					String name = player.getName();
					Bukkit.getServer().broadcastMessage(ChatColor.GREEN + user + " has married with " + name);
					cfg.set("Married." + name, user);
					cfg.set("Married." + user, name);
					List<String> list = cfg.getStringList("partners");
					list.add(user);
					cfg.set("partners", list);
					plugin.saveCustomConfig();
					MarryCommand.req.remove(name);
					return;
				}
				player.sendMessage(ChatColor.RED + "Player that requested you is not online");
				return;
			}
		}
		player.sendMessage(ChatColor.RED + "You dont got a request!");
	}
}
