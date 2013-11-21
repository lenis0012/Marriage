package com.lenis0012.bukkit.marriage.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lenis0012.bukkit.marriage.MPlayer;
import com.lenis0012.bukkit.marriage.lang.Messages;

public class ChatspyCommand extends MarryCommand {
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		MPlayer mp = plugin.getMPlayer(player);
		if(mp.isChatspy()) {
			mp.setChatspy(false);
			inform(player, Messages.LEFT_CHATSPY);
		} else {
			mp.setChatspy(true);
			inform(player, Messages.JOINED_CHATSPY);
		}
	}
	
	@Override
	public String getPermission() {
		return "marry.chatspy";
	}
	
	@Override
	public boolean playersOnly() {
		return true;
	}
}