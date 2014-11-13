package com.lenis0012.bukkit.marriage2.internal;

import java.util.UUID;

import com.lenis0012.bukkit.marriage2.MPlayer;
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

	@Override
	public MPlayer getMPlayer(UUID uuid) {
		//TODO: Everything...
		return null;
	}
}
