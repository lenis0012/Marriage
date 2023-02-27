package com.lenis0012.bukkit.marriage2.listeners;

import com.lenis0012.bukkit.marriage2.config.Permissions;
import com.lenis0012.bukkit.marriage2.internal.MarriageCore;
import com.lenis0012.pluginutils.updater.Updater;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdateListener implements Listener {
    private final MarriageCore core;

    public UpdateListener(MarriageCore core) {
        this.core = core;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        if(Permissions.UPDATE.has(player) && core.getUpdater() != null) {
            final Updater updater = core.getUpdater();
            Bukkit.getScheduler().runTaskLater(core.getPlugin(), () -> {
                if(player.isOnline()) {
                    updater.notifyIfUpdateAvailable(player);
                }
            }, 41L);
        }
    }
}
