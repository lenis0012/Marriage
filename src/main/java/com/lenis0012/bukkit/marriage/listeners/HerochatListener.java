package com.lenis0012.bukkit.marriage.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.dthielke.herochat.ChannelChatEvent;
import com.lenis0012.bukkit.marriage.MPlayer;
import com.lenis0012.bukkit.marriage.Marriage;

public class HerochatListener implements Listener {
	private Marriage plugin;
	
	public HerochatListener(Marriage plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onChannelChat(ChannelChatEvent event) {
		Player player = event.getSender().getPlayer();
		MPlayer mp = plugin.getMPlayer(player);
		if(mp.isMarried()) {
			if(plugin.getConfig().getBoolean("settings.chat-prefix.use")) {
				String format = plugin.getConfig().getString("settings.chat-prefix.format");
				format = plugin.fixColors(format);
				format = format.replace("{OLD_FORMAT}", event.getFormat());
				event.setFormat(format);
			}
		}
	}
}