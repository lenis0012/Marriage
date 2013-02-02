package me.lenis0012.mr.listeners;

import me.lenis0012.mr.MPlayer;
import me.lenis0012.mr.Marriage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerListener implements Listener {
	private Marriage plugin;
	public PlayerListener(Marriage i) { plugin = i; }
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onPlayerChat(AsyncPlayerChatEvent event)
	{
		Player player = event.getPlayer();
		String pname = player.getName();
		MPlayer mp = plugin.getMPlayer(player);
		
		if(plugin.chat.contains(pname))
		{
			Player partner = Bukkit.getServer().getPlayer(mp.getPartner());
			if(partner == null)
			{
				plugin.chat.remove(pname);
				return;
			}
			if(!partner.isOnline())
			{
				plugin.chat.remove(pname);
				return;
			}
			
			String message = event.getMessage();
			String format = plugin.getConfig().getString("settings.private-chat.format");
			format = format.replace("{Player}", pname);
			format = format.replace("{Message}", message);
			format = plugin.fixColors(format);
			partner.sendMessage(format);
			player.sendMessage(format);
			plugin.getLogger().info("[Marriage] Chat: "+pname+": "+message);
			event.setCancelled(true);
		}
	}
}
