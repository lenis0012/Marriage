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
	 * Needs its own group
	 */
	MAZEN,
	/**
	 * Rofl
	 */
	ATTACK_HELICOPTER,
	/**
	 * Not set
	 */
	UNKNOWN;
	
	public String getChatPrefix() {
		switch(this) {
			case MAZEN:
				return "&bM ";
			case ATTACK_HELICOPTER:
				return "&ePEW ";
			default:
				return MarriagePlugin.getInstance().getBukkitConfig("config.yml").get(toString().toLowerCase(), String.class);
		}
	}
}