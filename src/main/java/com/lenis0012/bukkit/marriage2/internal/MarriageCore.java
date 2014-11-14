package com.lenis0012.bukkit.marriage2.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.commands.CommandMarry;
import com.lenis0012.bukkit.marriage2.config.Message;
import com.lenis0012.bukkit.marriage2.config.Settings;
import com.lenis0012.bukkit.marriage2.internal.data.DataManager;
import com.lenis0012.bukkit.marriage2.internal.data.MarriageData;
import com.lenis0012.bukkit.marriage2.internal.data.MarriagePlayer;
import com.lenis0012.bukkit.marriage2.listeners.DatabaseListener;

public class MarriageCore extends MarriageBase {
	private final Map<UUID, MarriagePlayer> players = Collections.synchronizedMap(new HashMap<UUID, MarriagePlayer>());
	private DataManager dataManager;
	
	public MarriageCore(MarriagePlugin plugin) {
		super(plugin);
	}
	
	@Register(name = "config", type = Register.Type.ENABLE, priority = 0)
	public void loadConfig() {
		plugin.saveDefaultConfig();
		Settings.reloadAll(this);
		Message.reloadAll(this);
	}
	
	@Register(name = "database", type = Register.Type.ENABLE)
	public void loadDatabase() {
		this.dataManager = new DataManager(this, plugin.getConfig());
	}
	
	@Register(name = "listeners", type = Register.Type.ENABLE)
	public void registerListeners() {
		register(new DatabaseListener(this));
	}
	
	@Register(name = "commands", type = Register.Type.ENABLE)
	public void registerCommands() {
		register(CommandMarry.class);
	}

	@Override
	public MPlayer getMPlayer(UUID uuid) {
		MarriagePlayer player = players.get(uuid);
		if(player == null) {
			player = dataManager.loadPlayer(uuid);
			players.put(uuid, player);
		}
		
		return player;
	}

	@Override
	public void marry(MPlayer player1, MPlayer player2) {
		MarriageData mdata = new MarriageData(player1.getUniqueId(), player2.getUniqueId());
		((MarriagePlayer) player1).addMarriage(mdata);
		((MarriagePlayer) player2).addMarriage(mdata);
	}
}
