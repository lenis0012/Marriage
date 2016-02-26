package com.lenis0012.bukkit.marriage2.commands;

import com.lenis0012.bukkit.marriage2.MData;
import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.Marriage;
import com.lenis0012.bukkit.marriage2.config.Message;
import com.lenis0012.bukkit.marriage2.config.Settings;

public class CommandSethome extends Command {

	public CommandSethome(Marriage marriage) {
		super(marriage, "sethome");
		setExecutionFee(Settings.PRICE_SETHOME);
	}

	@Override
	public void execute() {
		MPlayer mPlayer = marriage.getMPlayer(player.getUniqueId());
		MData marriage = mPlayer.getMarriage();
		if(marriage == null) {
			reply(Message.NOT_MARRIED);
			return;
		}

		if(!payFee()) return;
		marriage.setHome(player.getLocation().clone());
		reply(Message.HOME_SET);
	}
}