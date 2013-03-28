package me.lenis0012.mr.commands;

import me.lenis0012.mr.MPlayer;

import org.bukkit.command.CommandSender;

public class ReloadCommand extends CommandBase {

	@Override
	public void perform(CommandSender sender, String[] args) {
		plugin.reloadConfig();
		inform(sender, "Reloaded config.yml!");
		plugin.reloadCustomConfig();
		inform(sender, "Reloaded data.yml!");
		
		for(MPlayer mp : plugin.getLoadedPlayers()) {
			mp.getConfig().reload();
		}
		
		inform(sender, "Reloaded player yml files!");
	}

	@Override
	public String getPermission() {
		return "marry.reload";
	}

	@Override
	public boolean playersOnly() {
		return false;
	}
}