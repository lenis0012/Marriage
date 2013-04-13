package me.lenis0012.mr;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface MPlayer extends Player {
	
	/**
	 * Check if a player is married
	 * 
	 * @return			player is married?
	 */
	public boolean isMarried();
	
	/**
	 * Get the players partner
	 * 
	 * @return			partner
	 */
	public String getPartner();
	
	/**
	 * Set the player married with a partner
	 * 
	 * @param user		partner
	 */
	public void setPartner(String user);
	
	/**
	 * Divorce the player
	 */
	public void divorce();
	
	/**
	 * Set if the player should be in private chat
	 * 
	 * @param value		value
	 */
	public void setChatting(boolean value);
	
	/**
	 * Check if the player is in a marry chat
	 * 
	 * @return player 	chatting?
	 */
	public boolean isChatting();
	
	/**
	 * Set the player's home
	 * 
	 * @param loc Location
	 */
	public void setHome(Location loc);
	
	/**
	 * Get the player's home
	 * 
	 * @return Location
	 */
	public Location getHome();
	
	/**
	 * Get the player's config file
	 * 
	 * @return Config
	 */
	public PlayerConfig getConfig();
}
