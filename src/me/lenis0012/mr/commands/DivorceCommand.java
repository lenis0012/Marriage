package me.lenis0012.mr.commands;

import me.lenis0012.mr.MPlayer;
import me.lenis0012.mr.Marriage;
import me.lenis0012.mr.util.EcoUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DivorceCommand
{
	public static void perfrom(Player player)
	{
		Marriage plugin = Marriage.instance;
		MPlayer mp = plugin.getMPlayer(player);
		if(!mp.isMarried())
		{
			player.sendMessage(ChatColor.RED+"You are not married");
			return;
		}
		if(plugin.eco) {
			double a = EcoUtil.getPriceFromConfig("divorce");
			if(a != 0.0) {
				if(EcoUtil.withrawMoneyIfEnough(player, a)) {
					return;
				}
			}
		}
		
		String user = player.getName();
		String partner = mp.getPartner();
		Player op = Bukkit.getPlayer(partner);
		
		//Chat fix start
		if(mp.isChatting())
			mp.setChatting(false);
		if(op != null && op.isOnline()) {
			MPlayer omp = plugin.getMPlayer(op);
			if(omp.isChatting())
				omp.setChatting(false);
		}
		//chat fix end
		
		mp.divorce();
		Bukkit.getServer().broadcastMessage(ChatColor.RED+user+" divorced with "+partner);
	}
}
