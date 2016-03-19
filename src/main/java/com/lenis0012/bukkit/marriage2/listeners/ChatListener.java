package com.lenis0012.bukkit.marriage2.listeners;

import com.lenis0012.bukkit.marriage2.MPlayer;
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
    private final MarriageCore core;

    public ChatListener(MarriageCore core) {
        this.core = core;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        MPlayer mp = core.getMPlayer(player.getUniqueId());
        if(mp.isInChat()) {
            if(!isOnline(mp.getPartner())) {
                mp.setInChat(false);
                return;
            }

            event.setCancelled(true);
            String message = Settings.PM_FORMAT.value()
                    .replace("{name}", player.getDisplayName())
                    .replace("{message}", event.getMessage())
                    .replace("{heart}", "\u2764");
            message = formatIcons(message);
            message = ChatColor.translateAlternateColorCodes('&', message);

            Player partner = Bukkit.getPlayer(mp.getPartner().getUniqueId());
            player.sendMessage(message);
            partner.sendMessage(message);
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChatLate(AsyncPlayerChatEvent event) {
        String format = event.getFormat();
        final Player player = event.getPlayer();
        final MPlayer mplayer = core.getMPlayer(player.getUniqueId());

        if(Settings.FORCE_FORMAT.value()) {
            format = "{marriage_status}" + event.getFormat(); // Enforce marriage format
        }

        // Marriage status
        if(format.contains("{marriage_status}")) {
            String status = "";
            if(mplayer.isMarried()) {
                String partner = player.hasMetadata("marriedTo") ? player.getMetadata("marriedTo").get(0).asString() : "";
                status = Settings.CHAT_FORMAT.value()
                        .replace("{heart}", "\u2764")
                        .replace("{partner}", partner);
                status = formatIcons(status);

                status = ChatColor.translateAlternateColorCodes('&', status);
            }
            event.setFormat(format.replace("{marriage_status}", status));
        }

        // Gender format
        if(format.contains("{marriage_gender}")) {
            String gender = mplayer.getGender().getChatPrefix();
            gender = formatIcons(gender);
            gender = ChatColor.translateAlternateColorCodes('&', gender);
            event.setFormat(format.replace("{marriage_gender}", gender));
        }
    }

    private String formatIcons(String text) {
        return text.replace("{heart}", "\u2764")
                .replace("{icon:heart}", "\u2764")
                .replace("{icon:male}", "\u2642")
                .replace("{icon:female}", "\u2640")
                .replace("{icon:genderless}", "\u26B2");
    }

    private boolean isOnline(MPlayer mp) {
        if(mp == null) return false;
        Player player = Bukkit.getPlayer(mp.getUniqueId());
        return player != null && player.isOnline();
    }
}
