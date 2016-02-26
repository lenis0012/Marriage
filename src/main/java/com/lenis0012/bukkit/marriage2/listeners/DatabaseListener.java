package com.lenis0012.bukkit.marriage2.listeners;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.internal.data.MarriagePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.lenis0012.bukkit.marriage2.internal.MarriageCore;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class DatabaseListener implements Listener {
	private final Cache<UUID, MarriagePlayer> cache = CacheBuilder.newBuilder().expireAfterWrite(30L, TimeUnit.SECONDS).build();
	private final MarriageCore core;
	
	public DatabaseListener(MarriageCore core) {
		this.core = core;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
		if(event.getLoginResult() == Result.ALLOWED) {
			cache.put(event.getUniqueId(), core.getDataManager().loadPlayer(event.getUniqueId()));
		}
	}  
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		final UUID userId = event.getPlayer().getUniqueId();
		MarriagePlayer player = cache.getIfPresent(userId);
		if(player != null) {
			core.setMPlayer(userId, cache.getIfPresent(userId));
			return;
		}

		// Something went wrong (unusually long login?)
		core.getLogger().log(Level.WARNING, "Player " + event.getPlayer().getName() + " was not in cache");
		core.getLogger().log(Level.INFO, "If this message shows often, report to dev");
		core.setMPlayer(userId, core.getDataManager().loadPlayer(userId));
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		core.unloadPlayer(player.getUniqueId());
	}
}