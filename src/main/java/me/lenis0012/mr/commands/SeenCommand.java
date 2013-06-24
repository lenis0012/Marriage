package me.lenis0012.mr.commands;

import me.lenis0012.mr.MPlayer;
import me.lenis0012.mr.PlayerConfig;
import me.lenis0012.mr.lang.Messages;
import me.lenis0012.mr.util.EcoUtil;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SeenCommand extends CommandBase {

	@Override
	public void perform(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		MPlayer mp = plugin.getMPlayer(player);
		Player op = Bukkit.getPlayer(mp.getPartner());
		
		if(!mp.isMarried()) {
			error(player, Messages.NO_PARTNER);
			return;
		}
		
		if(plugin.eco) {
			double a = EcoUtil.getPriceFromConfig("seen");
			if(a != 0.0) {
				if(EcoUtil.withrawMoneyIfEnough(player, a)) {
					return;
				}
			}
		}
		
		PlayerConfig pc = plugin.getPlayerConfig(mp.getPartner());
		String msg = Messages.OFFLINE_SINCE;
		if(op != null && op.isOnline())
			msg = Messages.ONLINE_SINCE;
		
		String type = Messages.SECONDS;
		long time = System.currentTimeMillis();
		if(op != null && op.isOnline())
			time -= pc.getLong("last-login");
		else
			time -= pc.getLong("last-logout");
		time = time / 1000;
		if(time >= 60) {
			time = time / 60;
			type = Messages.MINUTES;
		} if(time >= 60 && type == Messages.MINUTES) {
			time = time / 60;
			type = Messages.HOURS;
		} if(time >= 24 && type == Messages.HOURS) {
			time = time / 24;
			type = Messages.DAYS;
		} if(time >= 7 && type == Messages.DAYS) {
			time = time / 7;
			type = Messages.WEEKS;
		} if(time >= 4 && type == Messages.WEEKS) {
			time = time / 4;
			type = Messages.MONTHS;
		} if(time >= 12 && type == Messages.MONTHS) {
			time = time / 12;
			type = Messages.YEARS;
		}
		
		msg = msg.replace("{TIME}", time + " " + type);
		inform(player, msg);
	}

	@Override
	public String getPermission() {
		return "marry.seen";
	}

	@Override
	public boolean playersOnly() {
		return true;
	}
}