package com.lenis0012.bukkit.marriage2;

import com.lenis0012.bukkit.marriage2.internal.MarriagePlugin;

public enum Gender {
	/**
	 * Opposite of femaile
	 */
	MALE,
	/**
	 * Opposite of male
	 */
	FEMALE,
	/**
	 * Not set
	 */
	UNKNOWN;
	
	public String getChatPrefix() {
		switch(this) {
			default:
				return MarriagePlugin.getCore().getBukkitConfig("config.yml").get(toString().toLowerCase(), String.class);
		}
	}
}