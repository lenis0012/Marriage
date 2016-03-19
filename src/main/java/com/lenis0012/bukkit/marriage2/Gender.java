package com.lenis0012.bukkit.marriage2;

import com.lenis0012.bukkit.marriage2.config.Settings;

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

	/**
	 * Get chat prefix for gender.
	 *
	 * @return Chat prefix
     */
	public String getChatPrefix() {
		switch(this) {
			case MALE:
				return Settings.PREFIX_MALE.value();
			case FEMALE:
				return Settings.PREFIX_FEMALE.value();
			default:
				return Settings.PREFIX_GENDERLESS.value();
		}
	}
}