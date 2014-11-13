package com.lenis0012.bukkit.marriage2;

import java.util.UUID;

import org.bukkit.Location;

public interface MData {
	
	UUID getPlayer1Id();
	
	UUID getPllayer2Id();
	
	/**
	 * Get the home of the married people.
	 * 
	 * @return Marriage home.
	 */
	Location getHome();
	
	/**
	 * Set the home of the married people.
	 * 
	 * @return Marriage home.
	 */
	Location setHome();
	
	/**
	 * Check if the married players have a home set.
	 * 
	 * @return Whether or not a home is set for the married players.
	 */
	boolean isHomeSet();
	
	/**
	 * Check if PVP is enabled between the married players.
	 * 
	 * @return Whether or not pvp is enabled between the married players.
	 */
	boolean isPVPEnabled();
	
	/**
	 * Set if pvp is enabled between the married players.
	 * 
	 * @param pvpEnabled Whether or not pvp is enabled between the married players.
	 */
	void setPVPEnabled(boolean pvpEnabled);
}
