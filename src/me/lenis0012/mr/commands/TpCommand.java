package me.lenis0012.mr.commands;

import me.lenis0012.mr.MPlayer;
import me.lenis0012.mr.Marriage;
import me.lenis0012.mr.util.EcoUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpCommand extends CommandBase {
	public static void perfrom(Player player)
	{
		Marriage plugin = Marriage.instance;
		MPlayer mp = plugin.getMPlayer(player);
		if(!mp.isMarried())
		{
			player.sendMessage(ChatColor.RED + "You dont have a partner.");
			return;
		}
		Player op = Bukkit.getServer().getPlayer(mp.getPartner());
		if(op == null)
		{
			player.sendMessage(ChatColor.RED + "Your partner is not online");
			return;
		}
		if(!op.isOnline())
		{
			player.sendMessage(ChatColor.RED + "Your partner is not online");
			return;
		}
		if(!player.hasPermission("marry.tp") && !player.hasPermission("marry.*"))
		{
			player.sendMessage(ChatColor.RED + "No permission.");
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
		
		player.sendMessage(ChatColor.GREEN + "Teleporting...");
		op.sendMessage(ChatColor.GREEN + "Partner teleporting to you...");
		player.teleport(op.getLocation());
	}

	@Override
	public void perform(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		MPlayer mp = plugin.getMPlayer(player);
		Player op = Bukkit.getServer().getPlayer(mp.getPartner());
		
		if(!mp.isMarried()) {
			player.sendMessage(ChatColor.RED + "You dont have a partner.");
			return;
		}
		
		if(op == null || !op.isOnline()) {
			player.sendMessage(ChatColor.RED + "Your partner is not online");
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
		
		player.sendMessage(ChatColor.GREEN + "Teleporting...");
		op.sendMessage(ChatColor.GREEN + "Partner teleporting to you...");
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
