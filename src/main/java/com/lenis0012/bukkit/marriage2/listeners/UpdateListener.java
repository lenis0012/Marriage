package com.lenis0012.bukkit.marriage2.listeners;

import com.lenis0012.bukkit.marriage2.config.Message;
import com.lenis0012.bukkit.marriage2.config.Permissions;
import com.lenis0012.bukkit.marriage2.internal.MarriageCore;
import com.lenis0012.updater.api.Updater;
import com.lenis0012.updater.api.Version;
import net.md_5.bungee.api.ChatColor;
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
        if(Permissions.UPDATE.has(player)) {
            final Updater updater = core.getUpdater();
            Bukkit.getScheduler().runTaskLaterAsynchronously(core.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    boolean update = updater.hasUpdate();
                    if(update) {
                        Bukkit.getScheduler().runTask(core.getPlugin(), new Runnable() {
                            @Override
                            public void run() {
                                Version version = updater.getNewVersion();
                                String message = ChatColor.translateAlternateColorCodes('&',
                                        String.format(Message.UPDATE_AVAILABLE.toString(),
                                        version.getName(), version.getServerVersion()));
                                player.sendMessage(message);
                            }
                        });
                    }
                }
            }, 41L);
        }
    }
}
