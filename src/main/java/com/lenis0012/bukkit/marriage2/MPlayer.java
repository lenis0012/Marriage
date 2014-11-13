package com.lenis0012.bukkit.marriage2;

import java.util.UUID;

import javax.annotation.Nullable;

public interface MPlayer {
	
	/**
	 * Get unique User Id of the player.
	 * 
	 * @return Player's unique user id.
	 */
	UUID getUniqueId();
	
	/**
	 * Get the player's gender.
	 * 
	 * @return Player's gender.
	 */
	Gender getGender();
	
	/**
	 * Set the player's gender.
	 * 
	 * @param gender Player's gender.
	 */
	void setGender(Gender gender);
	
	/**
	 * Get current marriage of the player.
	 * 
	 * @return Player's marriage, NULL if not married.
	 */
	@Nullable
	MData getMarriage();
	
	/**
	 * Check if the player is married.
	 * 
	 * @return True if married, false otherwise.
	 */
	boolean isMarried();
	
	/**
	 * Check if the player is in marry-chat mode.
	 * 
	 * @return True if in chat-mode, false otherwise.
	 */
	boolean isInChat();
	
	/**
	 * Set if the player is in marry chat-mode.
	 * 
	 * @param inChat Whether or not the player is in marry chat-mode.
	 */
	void setInChat(boolean inChat);
}