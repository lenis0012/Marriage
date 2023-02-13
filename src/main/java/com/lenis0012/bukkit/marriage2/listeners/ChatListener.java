package com.lenis0012.bukkit.marriage2.listeners;

import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.config.Permissions;
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

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        MPlayer mp = core.getMPlayer(player);
        if(!mp.isInChat()) {
            return;
        }

        // Private chat
        if(!mp.isMarried() || !isOnline(mp.getPartner())) {
            mp.setInChat(false);
            return;
        }

        String message = Settings.PM_FORMAT.value()
                .replace("{name}", "%1$s")
                .replace("{heart}", "\u2764");
        message = formatIcons(message);
        message = ChatColor.translateAlternateColorCodes('&', message);
        message = message.replace("{message}", "%2$s");

        Player partner = Bukkit.getPlayer(mp.getPartner().getUniqueId());
        event.getRecipients().clear();
        event.getRecipients().add(player);
        event.getRecipients().add(partner);
        event.setFormat(message);
        if (Permissions.CHAT_COLOR.has(player)) {
            event.setMessage(ChatColor.translateAlternateColorCodes('&', event.getMessage()));
        }

        if (Settings.CHAT_BYPASS_PLUGINS.value()) {
            event.setCancelled(true);
            player.sendMessage(String.format(event.getFormat(), player.getName(), event.getMessage()));
            partner.sendMessage(String.format(event.getFormat(), player.getName(), event.getMessage()));
        }

        // Admin chat spy
        String adminMessage = null; // No need to format message if we're not going to send it.
        for(Player admin : Bukkit.getOnlinePlayers()) {
            if(admin.equals(player) || admin.equals(partner)) continue;
            final MPlayer mAdmin = core.getMPlayer(admin);
            if(!mAdmin.isChatSpy()) continue;
            if(adminMessage == null) {
                // Format message
                adminMessage = Settings.CHATSPY_FORMAT.value()
                        .replace("{sender}", player.getName())
                        .replace("{receiver}", partner.getName());
                adminMessage = formatIcons(adminMessage);
                adminMessage = ChatColor.translateAlternateColorCodes('&', adminMessage)
                        .replace("{message}", event.getMessage());
            }
            admin.sendMessage(adminMessage);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChatLate(AsyncPlayerChatEvent event) {
        String format = event.getFormat();
        final Player player = event.getPlayer();
        final MPlayer mplayer = core.getMPlayer(player);

        if(Settings.FORCE_FORMAT.value()) {
            format = "{marriage_status}" + format; // Enforce marriage format
        } if(Settings.FORCE_GENDER_FORMAT.value() && Settings.GENDERS_ENABLED.value()) {
            format = "{marriage_gender}" + format;
        }

        // Marriage status
        if(format.contains("{marriage_status}")) {
            String status = Settings.CHAT_FORMAT_UNMARRIED.value();
            if(mplayer.isMarried()) {
                String partner =
                        player.hasMetadata("marriedTo") && player.getMetadata("marriedTo").size() > 0
                        ? player.getMetadata("marriedTo").get(0).asString() : "";

                status = Settings.CHAT_FORMAT.value()
                        .replace("{heart}", "\u2764")
                        .replace("{partner}", partner);

                status = formatIcons(status);
            }

            if(!status.isEmpty()) {
                status = ChatColor.translateAlternateColorCodes('&', status);
            }

            format = format.replace("{marriage_status}", status);
        }

        // Gender format
        if(format.contains("{marriage_gender}")) {
            String gender = mplayer.getGender().getChatPrefix();
            gender = formatIcons(gender);
            gender = ChatColor.translateAlternateColorCodes('&', gender);
            format = format.replace("{marriage_gender}", gender);
        }

        // Set format
        event.setFormat(format);
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
