package me.lenis0012.mr.commands;

import me.lenis0012.mr.MPlayer;
import me.lenis0012.mr.lang.Messages;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class AcceptCommand extends CommandBase {

	@Override
	public void perform(CommandSender sender, String[] args) {
		if(plugin.req.containsKey(sender.getName())) {
			Player op = Bukkit.getServer().getPlayer(plugin.req.get(sender.getName()));
			if(op != null) {
				if(op.isOnline()) {
					String user = op.getName();
					String name = sender.getName();
					MPlayer mp = plugin.getMPlayer((Player) sender);
					mp.setPartner(user);
					
					String msg = Messages.MARRIED.replace("{USER1}", user).replace("{USER2}", name);
					Bukkit.getServer().broadcastMessage(ChatColor.GREEN + msg);
					plugin.req.remove(name);
					return;
				}
				error(sender, Messages.NOT_ONLINE);
				return;
			}
		}
		error(sender, Messages.NO_REQUEST);
	}

	@Override
	public String getPermission() {
		return null;
	}

	@Override
	public boolean playersOnly() {
		return true;
	}
}
