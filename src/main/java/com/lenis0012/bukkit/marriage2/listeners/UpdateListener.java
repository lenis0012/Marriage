package com.lenis0012.bukkit.marriage2.listeners;

import com.lenis0012.bukkit.marriage2.config.Permissions;
import com.lenis0012.bukkit.marriage2.internal.MarriagePlugin;
import com.lenis0012.pluginutils.updater.Updater;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdateListener implements Listener {
    private final MarriagePlugin plugin;

    public UpdateListener(MarriagePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        if(Permissions.UPDATE.has(player) && plugin.getUpdater() != null) {
            final Updater updater = plugin.getUpdater();
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(player.isOnline()) {
                    updater.notifyIfUpdateAvailable(player);
                }
            }, 41L);
        }
    }
}
