package com.lenis0012.bukkit.marriage.commands;

import java.util.List;


import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.lenis0012.bukkit.marriage.PlayerConfig;
import com.lenis0012.bukkit.marriage.lang.Messages;


public class ListCommand extends CommandBase {

	@Override
	public void perform(CommandSender sender, String[] args) {
		int page = 1;
		if(args.length == 2)
			page = Integer.valueOf(args[1]);
		
		List<String> list = plugin.getCustomConfig().getStringList("partners");
		
		if(list.isEmpty()) {
			error(sender, Messages.NO_PARTNERS);
			return;
		}
		
		int maxPage = 0;
		if(String.valueOf(list.size()).endsWith("0"))
		{
			maxPage = list.size() / 10;
		}else
			maxPage = Integer.valueOf(((Double) ((double)(list.size() / 10))).intValue() + 1);
		String pages = ChatColor.GOLD + Messages.PAGE + " " + String.valueOf(page) + "/" + String.valueOf(maxPage);
		inform(sender, pages);
		inform(sender, ChatColor.BLUE+ Messages.PARTNERS + ":");
		int msw = page > 1 ? 1 : 0;
		for(int i = page * 10 - 10 - msw; i < page * 10 && i < list.size(); i++) {
			String p1 = list.get(i);
			PlayerConfig cfg = plugin.getPlayerConfig(p1);
			String p2 = cfg.getString("partner");
			inform(sender, p1 + ChatColor.WHITE+" + " + ChatColor.GREEN + p2);
		}
	}

	@Override
	public String getPermission() {
		return null;
	}

	@Override
	public boolean playersOnly() {
		return false;
	}
}
