package com.lenis0012.bukkit.marriage2.config;

import com.lenis0012.bukkit.marriage2.Marriage;
import com.lenis0012.bukkit.marriage2.misc.BConfig;

public enum Message {
	PLAYER_NOT_FOUND("&cNo player named %s was found!"),
	TARGET_ALREADY_MARRIED("&cPlayers %s is already married to someone!"),
	ALREADY_MARRIED("&cYou are already married to someone!"),
	MARRIED("&a&lPlayer %s and %s have just married!"),
	MARRIAGE_REQUESTED("&aPlayer %s has requested you to marry with them, use &e/marry %s &ato accept it."),
	NOT_MARRIED("&cYou are currently not married with someone!"),
	DIVORCED("&aPlayer %s and %s have divorced!"),
	HOME_TELEPORT("&aYou have been teleported to your marriage home!"),
	HOME_NOT_SET("&cYou currently do not have a home set for your marriage!"),
	HOME_SET("&aYou have set a home for your marriage!");
	
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