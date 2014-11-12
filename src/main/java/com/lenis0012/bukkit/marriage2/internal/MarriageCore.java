package com.lenis0012.bukkit.marriage2.internal;

import com.lenis0012.bukkit.marriage2.commands.CommandTest;
import com.lenis0012.bukkit.marriage2.lang.Message;

public class MarriageCore extends MarriageBase {

	public MarriageCore(MarriagePlugin plugin) {
		super(plugin);
	}
	
	@Register(name = "commands", type = Register.Type.ENABLE)
	public void registerCommands() {
		register(CommandTest.class);
	}
	
	@Register(name = "messages", type = Register.Type.ENABLE, priority = 1)
	public void loadMessages() {
		Message.reloadAll(this);
	}
}
