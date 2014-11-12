package com.lenis0012.bukkit.marriage2.commands;

import org.bukkit.entity.Player;

import com.lenis0012.bukkit.marriage2.lang.Message;

public class CommandMarry extends Command {
	
	public CommandMarry(){
		super("marry");
		setDescription("Request a marriage with another player.");
		setUsage("<player>");
		
		setMinArgs(0);
		setAllowConsole(false);
	}
	
	@Override
	public void execute() {
		Player target = getArgAsPlayer(-1);
		if(target != null) {
			if(System.out != null) { //TODO: Check if married
				//TODO: Handle command delay
				//TODO: Send marry request
			} else {
				reply(Message.ALREADY_MARRIED, getArg(-1));
			}
		} else {
			reply(Message.PLAYER_NOT_FOUND, getArg(-1));
		}
	}
}