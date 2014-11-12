package com.lenis0012.bukkit.marriage2.lang;

import com.lenis0012.bukkit.marriage2.Marriage;
import com.lenis0012.bukkit.marriage2.misc.BConfig;

public enum Message {
	PLAYER_NOT_FOUND("&cNo player named %s was found!"),
	ALREADY_MARRIED("&cPlayers %s is already married to someone!");
	
	private final String defaultMessage;
	private String message;
	
	private Message(String def) {
		this.defaultMessage = def;
	}
	
	private void reload(BConfig config) {
		this.message = config.getOrSet(toString().toLowerCase(), defaultMessage);
	}
	
	@Override
	public String toString() {
		return message;
	}
	
	public static void reloadAll(Marriage marriage) {
		BConfig config = marriage.getBukkitConfig("messages.yml");
		for(Message message : Message.values()) {
			message.reload(config);
		}
		
		config.save();
	}
}