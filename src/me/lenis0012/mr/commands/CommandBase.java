package me.lenis0012.mr.commands;

import me.lenis0012.mr.Marriage;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class CommandBase {
	private boolean isPlayer;
	Marriage plugin = Marriage.instance;
	
	public final void execute(CommandSender sender, String[] args) {
		this.isPlayer = sender instanceof Player;
		
		if(this.playersOnly() && !this.isPlayer) {
			error(sender, "You must be a player to execute this command");
			return;
		}
		
		if(this.getPermission() != null && !sender.hasPermission(this.getPermission())) {
			error(sender, "Invalid permissions!");
			return;
		}
		
		this.perform(sender, args);
	}
	
	void error(CommandSender sender, String msg) {
		String color = this.isPlayer ? ChatColor.RED.toString() : "";
		sender.sendMessage(color + msg);
	}
	
	void inform(CommandSender sender, String msg) {
		String color = this.isPlayer ? ChatColor.GREEN.toString() : "";
		sender.sendMessage(color + msg);
	}
	
	boolean isPlayer() {
		return this.isPlayer;
	}
	
	public abstract void perform(CommandSender sender, String[] args);
	
	public abstract String getPermission();
	
	public abstract boolean playersOnly();
}
