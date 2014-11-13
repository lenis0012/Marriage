package com.lenis0012.bukkit.marriage2.internal;

import java.util.UUID;

import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.commands.CommandMarry;
import com.lenis0012.bukkit.marriage2.config.Message;
import com.lenis0012.bukkit.marriage2.config.Settings;

public class MarriageCore extends MarriageBase {

	public MarriageCore(MarriagePlugin plugin) {
		super(plugin);
	}
	
	@Register(name = "config", type = Register.Type.ENABLE, priority = 0)
	public void loadConfig() {
		Settings.reloadAll();
	}
	
	@Register(name = "messages", type = Register.Type.ENABLE, priority = 1)
	public void loadMessages() {
		Message.reloadAll(this);
	}
	
	@Register(name = "commands", type = Register.Type.ENABLE)
	public void registerCommands() {
		register(CommandMarry.class);
	}


	@Override
	public MPlayer getMPlayer(UUID uuid) {
		// TODO: Everything...
		return null;
	}

	@Override
	public void marry(MPlayer player1, MPlayer player2) {
		// TODO: Marry player1 with player2
	}
}
