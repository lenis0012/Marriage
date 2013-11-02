package com.lenis0012.bukkit.marriage.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lenis0012.bukkit.marriage.MPlayer;
import com.lenis0012.bukkit.marriage.lang.Messages;
import com.lenis0012.bukkit.marriage.util.EcoUtil;



public class MarryCommand extends CommandBase {

	@Override
	public void perform(CommandSender sender, String[] args) {
		if(args.length <= 1) {
			Player player = (Player) sender;
			Player op = plugin.getPlayer(args[0]);
			if(op != null) {
				if(op.isOnline()) {
					MPlayer mp = plugin.getMPlayer(player);
					MPlayer tp = plugin.getMPlayer(op);
					
					if(op.getName().equals(player.getName())) {
						error(player, Messages.NOT_YOURSELF);
						return;
					}
					
					if(mp.isMarried()) {
						error(player, Messages.ALREADY_MARRIED);
						return;
					}
					
					if(tp.isMarried()) {
						String msg = Messages.HAS_PARTNER.replace("{USER}", op.getName());
						error(player, msg);
						return;
					}
					
					if(plugin.eco) {
						double a = EcoUtil.getPriceFromConfig("marry");
						if(a != 0.0) {
							if(EcoUtil.withrawMoneyIfEnough(player, a)) {
								return;
							}
						}
					}
					
					inform(player, Messages.REQUEST_SENT);
					String cmd = ChatColor.LIGHT_PURPLE + "/marry accept" + ChatColor.GREEN;
					String msg = Messages.REQUEST_RECEIVED.replace("{USER}", player.getName()).replace("{COMMAND}", cmd);
					inform(op, msg);
					plugin.req.put(op.getName(), player.getName());
					return;
				}
			}
		} else {
			Player priest = (Player) sender;
			Player p1 = plugin.getPlayer(args[0]);
			Player p2 = plugin.getPlayer(args[2]);
			MPlayer mp1 = plugin.getMPlayer(p1);
			MPlayer mp2 = plugin.getMPlayer(p2);
			if(!priest.hasPermission("marry.priest")) {
				error(priest, Messages.NO_PERMISSION);
				return;
			}
			
			if(p1 == null || !p1.isOnline() || p2 == null || !p2.isOnline()) {
				error(priest, Messages.INVALID_PLAYER);
				return;
			}
			
			if(mp1.isMarried() || mp2.isMarried()) {
				error(priest, Messages.ALREADY_MARRIED);
				return;
			}
			
			mp1.setPartner(p2.getName());
			String msg = Messages.MARRIED.replace("{USER1}", p1.getName()).replace("{USER2}", p2.getName());
			Bukkit.getServer().broadcastMessage(ChatColor.GREEN + msg);
		}
		
		error(sender, Messages.INVALID_PLAYER);
	}

	@Override
	public String getPermission() {
		return "marry.marry";
	}

	@Override
	public boolean playersOnly() {
		return true;
	}
}
