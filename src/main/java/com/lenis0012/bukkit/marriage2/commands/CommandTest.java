package com.lenis0012.bukkit.marriage2.commands;

public class CommandTest extends Command {
	
	public CommandTest() {
		super("test");
		setDescription("A test command");
		setPermission("marry.command.test");
	}

	@Override
	public void execute() {
		reply("&aThis is a test command :D");
	}
}
