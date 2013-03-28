package me.lenis0012.mr.commands;

import me.lenis0012.mr.MPlayer;
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
					
					Bukkit.getServer().broadcastMessage(ChatColor.GREEN + user + " has married with " + name);
					plugin.req.remove(name);
					return;
				}
				error(sender, "Player that requested you is not online");
				return;
			}
		}
		error(sender, "You dont got a request!");
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
