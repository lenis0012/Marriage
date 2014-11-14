package com.lenis0012.bukkit.marriage2.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;

import com.lenis0012.bukkit.marriage2.internal.MarriageCore;

public class DatabaseListener implements Listener {
	private final MarriageCore core;
	
	public DatabaseListener(MarriageCore core) {
		this.core = core;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
		if(event.getLoginResult() == Result.ALLOWED) {
			core.getMPlayer(event.getUniqueId());
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		core.getMPlayer(event.getPlayer().getUniqueId());
	}
}