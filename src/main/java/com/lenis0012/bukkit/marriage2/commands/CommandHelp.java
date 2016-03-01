package com.lenis0012.bukkit.marriage2.commands;

import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.Marriage;
import com.lenis0012.bukkit.marriage2.config.Message;
import com.lenis0012.bukkit.marriage2.config.Settings;
import com.lenis0012.bukkit.marriage2.internal.MarriageBase;
import com.lenis0012.bukkit.marriage2.internal.MarriageCommandExecutor;
import mkremins.fanciful.FancyMessage;
import net.md_5.bungee.api.ChatColor;

public class CommandHelp extends Command {

	public CommandHelp(Marriage marriage) {
		super(marriage, "help");
		setMinArgs(-1);
		setHidden(true);
		setPermission(null);
        setAllowConsole(true);
	}

	@Override
	public void execute() {
		MarriageCommandExecutor commandExecutor = ((MarriageBase) marriage).getCommandExecutor();
//		reply("Author: &alenis0012");
		reply("Version: &a" + marriage.getPlugin().getDescription().getVersion());
		reply("&2&m---------&2< &a&lMarriage Command Help &2>&2&m---------"); // Play around with the amount of dashes later
		for(Command command : commandExecutor.getSubCommands()) {
			if(command.isHidden()) {
				continue;
			}

			String alias = command instanceof CommandMarry ? "" : command.getAliases()[0] + " ";
			String text = "&a/marry " + alias + command.getUsage() + " &f- &7" + command.getDescription();
			FancyMessage message = new FancyMessage(ChatColor.translateAlternateColorCodes('&', text));
			if(command.getExecutionFee() > 0.0) {
				message.tooltip("Cost: " + marriage.dependencies().getEconomyService().format(command.getExecutionFee()));
			}
			message.send(sender);
		}

		String status = Message.SINGLE.toString();
		if(player != null && player.hasMetadata("marriedTo")) {
			String partner = player.getMetadata("marriedTo").get(0).asString();
			status = String.format(Message.MARRIED_TO.toString(), partner);
		}
		reply(Message.STATUS, status);
		reply("&2&m--------------------------------------------"); // Play around with the amount of dashes later
	}
}
