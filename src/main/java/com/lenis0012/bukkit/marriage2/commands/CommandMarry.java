package com.lenis0012.bukkit.marriage2.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.Marriage;
import com.lenis0012.bukkit.marriage2.config.Message;

public class CommandMarry extends Command {
	
	public CommandMarry(Marriage marriage) {
		super(marriage, "marry");
		setDescription("Request a marriage with another player.");
		setUsage("<player>");
		
		setMinArgs(0);
		setAllowConsole(false);
	}
	
	@Override
	public void execute() {
		Player target = getArgAsPlayer(-1);
		if(target != null) {
			MPlayer mPlayer = marriage.getMPlayer(player.getUniqueId());
			if(!mPlayer.isMarried()) {
				MPlayer mTarget = marriage.getMPlayer(target.getUniqueId());
				if(!mTarget.isMarried()) {
					if(mPlayer.isMarriageRequested(target.getUniqueId())) {
						marriage.marry(mPlayer, mTarget);
						broadcast(Message.MARRIED, player.getName(), target.getName());
					} else {
						mTarget.requestMarriage(player.getUniqueId());
						target.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(Message.MARRIAGE_REQUESTED.toString(), player.getName(), player.getName())));
					}
				} else {
					reply(Message.TARGET_ALREADY_MARRIED, getArg(-1));
				}
			} else {
				reply(Message.ALREADY_MARRIED);
			}
		} else {
			reply(Message.PLAYER_NOT_FOUND, getArg(-1));
		}
	}
}