package me.lenis0012.mr.commands;

import me.lenis0012.mr.MPlayer;
import me.lenis0012.mr.lang.LangConfig;

import org.bukkit.command.CommandSender;

public class ReloadCommand extends CommandBase {

	@Override
	public void perform(CommandSender sender, String[] args) {
		plugin.reloadConfig();
		inform(sender, "Reloaded config.yml!");
		plugin.reloadCustomConfig();
		inform(sender, "Reloaded data.yml!");
		LangConfig.get().reload();
		inform(sender, "Reloaded lang.yml!");
		
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