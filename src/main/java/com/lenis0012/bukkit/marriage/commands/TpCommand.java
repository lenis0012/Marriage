package com.lenis0012.bukkit.marriage.commands;


import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lenis0012.bukkit.marriage.MPlayer;
import com.lenis0012.bukkit.marriage.lang.Messages;
import com.lenis0012.bukkit.marriage.util.EcoUtil;


public class TpCommand extends CommandBase {
	
	@Override
	public void perform(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		MPlayer mp = plugin.getMPlayer(player);
		Player op = Bukkit.getServer().getPlayer(mp.getPartner());
		
		if(!mp.isMarried()) {
			error(player, Messages.NO_PARTNER);
			return;
		}
		
		if(op == null || !op.isOnline()) {
			error(player, Messages.NOT_ONLINE);
			return;
		}
		
		if(plugin.eco) {
			double a = EcoUtil.getPriceFromConfig("tp");
			if(a != 0.0) {
				if(EcoUtil.withrawMoneyIfEnough(player, a)) {
					return;
				}
			}
		}
		
		inform(player, Messages.TELEPORTING + "...");
		inform(op, Messages.PARTNER_TELEPORTING);
		player.teleport(op.getLocation());
	}

	@Override
	public String getPermission() {
		return "marry.tp";
	}

	@Override
	public boolean playersOnly() {
		return true;
	}
}
