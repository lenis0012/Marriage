package me.lenis0012.mr.commands;

import me.lenis0012.mr.MPlayer;
import me.lenis0012.mr.lang.Messages;
import me.lenis0012.mr.util.EcoUtil;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomeCommand extends CommandBase {

	@Override
	public void perform(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		MPlayer mp = plugin.getMPlayer(player);
		
		if(!mp.isMarried()) {
			error(player, Messages.NO_PARTNER);
			return;
		}
		
		Location home = mp.getHome();
		
		if(home == null) {
			error(player, Messages.NO_HOME);
			return;
		}
		
		if(plugin.eco) {
			double a = EcoUtil.getPriceFromConfig("home");
			if(a != 0.0) {
				if(EcoUtil.withrawMoneyIfEnough(player, a)) {
					return;
				}
			}
		}
		
		player.teleport(home);
		inform(player, Messages.HOME_TP);
	}

	@Override
	public String getPermission() {
		return "marry.home";
	}

	@Override
	public boolean playersOnly() {
		return true;
	}
}
