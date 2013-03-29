package me.lenis0012.mr.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.lenis0012.mr.MPlayer;
import me.lenis0012.mr.lang.Messages;
import me.lenis0012.mr.util.EcoUtil;

public class MarryCommand extends CommandBase {

	@Override
	public void perform(CommandSender sender, String[] args) {
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
				inform(player, msg);
				plugin.req.put(op.getName(), player.getName());
				return;
			}
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
