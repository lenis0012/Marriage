package com.lenis0012.bukkit.marriage2.listeners;

import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.config.Message;
import com.lenis0012.bukkit.marriage2.config.Settings;
import com.lenis0012.bukkit.marriage2.internal.MarriageCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
    private static final EventPriority PRIORITY = EventPriority.valueOf(Settings.CHAT_PRIORITY.value().toUpperCase());
    private final MarriageCore core;

    public ChatListener(MarriageCore core) {
        this.core = core;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        MPlayer mp = core.getMPlayer(player.getUniqueId());
        if(mp.isInChat()) {
//            event.setCancelled(true);
            if(!isOnline(mp.getPartner())) {
                mp.setInChat(false);
//                Message.PARTNER_NOT_ONLINE.send(player);
                return;
            }

            event.setCancelled(true);
            String message = Settings.PM_FORMAT.value()
                    .replace("{name}", player.getDisplayName())
                    .replace("{message}", event.getMessage())
                    .replace("{heart}", "\u2764");
            message = ChatColor.translateAlternateColorCodes('&', message);

            Player partner = Bukkit.getPlayer(mp.getPartner().getUniqueId());
            player.sendMessage(message);
            partner.sendMessage(message);
            return;
        }

//        if(Settings.CHAT_PRIORITY.value().toLowerCase().contains("high")) {
//            handleChat(event, mp);
//        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerChatLate(AsyncPlayerChatEvent event) {
//        if(!Settings.CHAT_PRIORITY.value().toLowerCase().contains("low") || event.isCancelled()) return;
//        final Player player = event.getPlayer();
//        MPlayer mp = core.getMPlayer(player.getUniqueId());
//        handleChat(event, mp);
    }

    private void handleChat(AsyncPlayerChatEvent event, MPlayer mp) {
        if(!mp.isMarried()) {
            return;
        }

        // Set format
        String format = Settings.CHAT_FORMAT.value()
                .replace("{name}", "%1$s")
                .replace("{message}", "%2$s")
                .replace("{original_format}", event.getFormat())
                .replace("{heart}", "\u2764");
        format = ChatColor.translateAlternateColorCodes('&', format);
        event.setFormat(format);
    }

    private boolean isOnline(MPlayer mp) {
        if(mp == null) return false;
        Player player = Bukkit.getPlayer(mp.getUniqueId());
        return player != null && player.isOnline();
    }
}
