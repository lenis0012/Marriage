package com.lenis0012.bukkit.marriage2;

import org.bukkit.Location;

import java.util.UUID;

public interface MData {

    UUID getPlayer1Id();

    UUID getPllayer2Id();

    /**
     * Get player 1 or 2 depending on the current player's UUID.
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
     * @return Whether a home is set for the married players.
     */
    boolean isHomeSet();

    /**
     * Check if PVP is enabled between the married players.
     *
     * @return Whether pvp is enabled between the married players.
     */
    boolean isPVPEnabled();

    /**
     * Set if pvp is enabled between the married players.
     *
     * @param pvpEnabled Whether pvp is enabled between the married players.
     */
    void setPVPEnabled(boolean pvpEnabled);
}
