package com.lenis0012.bukkit.marriage2;

import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Location;

public interface MData {
	
	UUID getPlayer1Id();
	
	UUID getPllayer2Id();
	
	/**
	 * Get player 1 or 2 depending in the current player's UUID.
	 * 
	 * @param me The current player
	 * @return The other player
	 */
	UUID getOtherPlayer(UUID me);
	
	/**
	 * Get the home of the married people.
	 * 
	 * @return Marriage home, NULL if not set.
	 */
	@Nullable
	Location getHome();
	
	/**
	 * Set the home of the married people.
	 *
	 * @param home The new home location
	 */
	void setHome(Location home);
	
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
