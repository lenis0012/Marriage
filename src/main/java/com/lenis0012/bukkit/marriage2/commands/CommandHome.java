package com.lenis0012.bukkit.marriage2.commands;

import org.bukkit.Location;

import com.lenis0012.bukkit.marriage2.MData;
import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.Marriage;
import com.lenis0012.bukkit.marriage2.config.Message;

public class CommandHome extends Command {

	public CommandHome(Marriage marriage) {
		super(marriage, "home");
		setDescription("Teleport to your marriage home");
	}

	@Override
	public void execute() {
		MPlayer mPlayer = marriage.getMPlayer(player.getUniqueId());
		MData marriage = mPlayer.getMarriage();
		if(marriage != null) {
			Location home = marriage.getHome();
			if(home != null) {
				player.teleport(home);
				reply(Message.HOME_TELEPORT);
			} else {
				reply(Message.HOME_NOT_SET);
			}
		} else {
			reply(Message.NOT_MARRIED);
		}
	}
}