package com.lenis0012.bukkit.marriage2.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lenis0012.bukkit.marriage2.Marriage;
import com.lenis0012.bukkit.marriage2.config.Message;

public abstract class Command {
	protected final Marriage marriage;
	private final String[] aliases;
	private String description = "No description available";
	private String usage = "";
	private int minArgs = 0;
	private String permission = null;
	private boolean allowConsole = false;
	private boolean hidden = false;
	
	protected CommandSender sender;
	protected Player player;
	private String[] args;
	
	public Command(Marriage marriage, String... aliases) {
		this.marriage = marriage;
		this.aliases = aliases;
		if(aliases.length > 0) {
			this.permission = "marry." + aliases[0];
		}
	}
	
	public abstract void execute();
	
	public void prepare(CommandSender sender, String[] args) {
		this.sender = sender;
		this.player = sender instanceof Player ? (Player) sender : null;
		this.args = args;
	}
	
	protected String getArg(int index) {
		return args[index + 1];
	}
	
	protected int getArgAsInt(int index) {
		return getArgAsInt(index, 0);
	}
	
	protected Player getArgAsPlayer(int index) {
		String name = getArg(index);
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(player.getName().equalsIgnoreCase(name)) {
				return player;
			}
		} for(Player player : Bukkit.getOnlinePlayers()) {
			if(player.getName().toLowerCase().contains(name.toLowerCase())) {
				return player;
			}
		}
		
		return null;
	}
	
	protected int getArgLength() {
		return args.length - 1;
	}
	
	protected int getArgAsInt(int index, int def) {
		try {
			return Integer.parseInt(getArg(index));
		} catch(Exception e) {
			return def;
		}
	}
	
	/**
	 * Reply to the command sender with a specified formatted message.
	 * 
	 * @param message Message to send.
	 * @param args Formatting arguments
	 */
	protected void reply(Message message, Object... args) {
		reply(sender, message, args);
	}
	
	/**
	 * Reply to the command sender with a formatted message.
	 * 
	 * @param message Message to send.
	 * @param args Formatting arguments
	 */
	protected void reply(String message, Object... args) {
		reply(sender, message, args);
	}
	
	protected void reply(CommandSender sender, Message message, Object... args) {
		reply(sender, message.toString(), args);
	}
	
	protected void reply(CommandSender sender, String message, Object... args) {
		message = ChatColor.translateAlternateColorCodes('&', String.format(message, args));
		sender.sendMessage(message);
	}
	
	protected void broadcast(Message message, Object... args) {
		broadcast(message.toString(), args);
	}
	
	protected void broadcast(String message, Object... args) {
		message = ChatColor.translateAlternateColorCodes('&', String.format(message, args));
		sender.sendMessage(message);
	}
	
	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String[] getAliases() {
		return aliases;
	}

	public String getDescription() {
		return description;
	}

	public String getUsage() {
		return usage;
	}

	public int getMinArgs() {
		return minArgs;
	}

	public boolean isAllowConsole() {
		return allowConsole;
	}

	protected void setDescription(String description) {
		this.description = description;
	}

	protected void setUsage(String usage) {
		this.usage = usage;
	}

	protected void setMinArgs(int minArgs) {
		this.minArgs = minArgs;
	}

	protected void setAllowConsole(boolean allowConsole) {
		this.allowConsole = allowConsole;
	}

	protected boolean isHidden() {
		return hidden;
	}

	protected void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
}