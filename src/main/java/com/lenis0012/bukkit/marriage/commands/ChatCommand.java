package com.lenis0012.bukkit.marriage.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lenis0012.bukkit.marriage.MPlayer;
import com.lenis0012.bukkit.marriage.lang.Messages;
import com.lenis0012.bukkit.marriage.util.EcoUtil;



public class ChatCommand extends CommandBase {
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		MPlayer mp = plugin.getMPlayer(player);
		if(!mp.isMarried()) {
			error(player, Messages.NO_PARTNER);
			return;
		}
		
		Player op = Bukkit.getServer().getPlayer(mp.getPartner());
		
		if(op == null || !op.isOnline()) {
			error(player, Messages.NOT_ONLINE);
			return;
		}
		
		if(plugin.eco) {
			double a = EcoUtil.getPriceFromConfig("chat");
			if(a != 0.0) {
				if(EcoUtil.withrawMoneyIfEnough(player, a)) {
					return;
				}
			}
		}
		
		if(mp.isChatting()) {
			mp.setChatting(false);
			error(player, Messages.LEFT_CHAT);
		} else {
			mp.setChatting(true);
			inform(player, Messages.JOINED_CHAT);
		}
	}

	@Override
	public String getPermission() {
		return "marry.chat";
	}

	@Override
	public boolean playersOnly() {
		return true;
	}
}
