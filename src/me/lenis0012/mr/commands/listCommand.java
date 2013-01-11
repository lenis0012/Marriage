package me.lenis0012.mr.commands;

import java.util.List;

import me.lenis0012.mr.Marriage;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class listCommand 
{
	public static void perform(Player player, String[] args)
	{
		Marriage plugin = Marriage.instance;
		int page = 1;
		if(args.length == 2)
			page = Integer.valueOf(args[1]);
		
		List<String> list = plugin.getCustomConfig().getStringList("partners");
		
		if(list.isEmpty())
		{
			player.sendMessage(ChatColor.RED+"There are no married players on this server");
			return;
		}
		
		String[] array = list.toArray(new String[list.size() - 1]);
		
		int maxPage = 0;
		if(String.valueOf(array.length).endsWith("0"))
		{
			maxPage = array.length / 10;
		}else
			maxPage = Integer.valueOf(((Double) ((double)(array.length / 10))).intValue() + 1);
		String pages = ChatColor.GOLD + "Page "+String.valueOf(page)+"/"+String.valueOf(maxPage);
		player.sendMessage(pages);
		player.sendMessage(ChatColor.BLUE+"Partners:");
		for(String p1 : array)
		{
			String p2 = plugin.getCustomConfig().getString("Married."+p1);
			player.sendMessage(ChatColor.GREEN+p1 +" "+ChatColor.WHITE+"+ "+ChatColor.GREEN+p2);
		}
	}
}
