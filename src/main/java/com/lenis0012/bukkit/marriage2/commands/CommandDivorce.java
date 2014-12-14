package com.lenis0012.bukkit.marriage2.commands;

import org.bukkit.Bukkit;

import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.Marriage;
import com.lenis0012.bukkit.marriage2.config.Message;

public class CommandDivorce extends Command {

	public CommandDivorce(Marriage marriage) {
		super(marriage, "divorce");
		setDescription("Divorce your current partner");
	}

	@Override
	public void execute() {
		MPlayer mPlayer = marriage.getMPlayer(player.getUniqueId());
		MPlayer partner = mPlayer.getPartner();
		if(partner != null) {
			mPlayer.divorce();
			broadcast(Message.DIVORCED, player.getName(), Bukkit.getOfflinePlayer(partner.getUniqueId()).getName());
		} else {
			reply(Message.NOT_MARRIED);
		}
	}
}
