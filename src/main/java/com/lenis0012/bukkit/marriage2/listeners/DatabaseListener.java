package com.lenis0012.bukkit.marriage2.listeners;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.internal.data.DataManager;
import com.lenis0012.bukkit.marriage2.internal.data.MarriagePlayer;
import com.lenis0012.bukkit.marriage2.misc.ListQuery;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.lenis0012.bukkit.marriage2.internal.MarriageCore;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
            MarriagePlayer player = core.getDataManager().loadPlayer(event.getUniqueId());
            player.setLastName(event.getName());
			cache.put(event.getUniqueId(), player);
		}
	}  
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
		final UUID userId = player.getUniqueId();
		MarriagePlayer mplayer = cache.getIfPresent(userId);
		if(mplayer != null) {
            loadPartnerName(mplayer, player);
			core.setMPlayer(userId, mplayer);
			return;
		}

		// Something went wrong (unusually long login?)
		core.getLogger().log(Level.WARNING, "Player " + event.getPlayer().getName() + " was not in cache");
		core.getLogger().log(Level.INFO, "If this message shows often, report to dev");
        mplayer = core.getDataManager().loadPlayer(userId);
        mplayer.setLastName(player.getName());
        loadPartnerName(mplayer, player);
		core.setMPlayer(userId, mplayer);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		core.unloadPlayer(player.getUniqueId());
        if(player.hasMetadata("marriedTo")) {
            player.removeMetadata("marriedTo", core.getPlugin());
        }
	}

    private void loadPartnerName(final MPlayer mplayer, final Player player) {
        if(!mplayer.isMarried()) return;
        DataManager.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                final String partner = ListQuery.getName(core.getDataManager(), mplayer.getMarriage().getOtherPlayer(player.getUniqueId()));
                if(partner == null) {
                    return;
                }

                Bukkit.getScheduler().runTask(core.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        player.setMetadata("marriedTo", new FixedMetadataValue(core.getPlugin(), partner));
                    }
                });
            }
        });
    }
}