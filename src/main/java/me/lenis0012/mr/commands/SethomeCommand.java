package me.lenis0012.mr.commands;

import me.lenis0012.mr.MPlayer;
import me.lenis0012.mr.lang.Messages;
import me.lenis0012.mr.util.EcoUtil;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class SethomeCommand extends CommandBase {

	@Override
	public void perform(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		MPlayer mp = plugin.getMPlayer(player);
		String partner = mp.getPartner();
		
		if(!mp.isMarried()) {
			error(player, Messages.NO_PARTNER);
			return;
		}
		
		if(plugin.eco) {
			double a = EcoUtil.getPriceFromConfig("sethome");
			if(a != 0.0) {
				if(EcoUtil.withrawMoneyIfEnough(player, a)) {
					return;
				}
			}
		}
		
		Location loc = player.getLocation();
		mp.setHome(loc);
		
		inform(player, Messages.HOME_SET);
		Player op = Bukkit.getPlayer(partner);
		if(op != null) {
			if(op.isOnline()) {
				inform(op, Messages.PARTNER_SETHOME);
			}
		}
	}

	@Override
	public String getPermission() {
		return "marry.sethome";
	}

	@Override
	public boolean playersOnly() {
		return true;
	}
}
