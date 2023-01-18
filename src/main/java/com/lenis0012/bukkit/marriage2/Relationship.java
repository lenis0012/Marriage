package com.lenis0012.bukkit.marriage2;

import org.bukkit.Location;

import java.util.UUID;

/**
 * Represents a relationship between two players.
 * This may be a marriage, or a relationship between two betrothed players.
 */
public interface Relationship {
    /**
     * Get the UUID of the player who proposed/initiated the relationship.
     *
     * @return UUID of player
     */
    UUID getPlayer1Id();

    /**
     * Get the UUID of the player who accepted the relationship.
     *
     * @return UUID of player
     */
    UUID getPllayer2Id();

    /**
     * Helper method to get player 1 or 2 depending on the current player's UUID.
     *
     * @param me The current player
     * @return The other player
     */
    UUID getOtherPlayer(UUID me);

    /**
     * Get the home location of the relationship.
     *
     * @return Marriage home, NULL if not set.
     */
    Location getHome();

    /**
     * Set the home of the relationship.
     *
     * @param home The new home location
     */
    void setHome(Location home);

    /**
     * @return Whether a home is set for this relationship.
     */
    boolean isHomeSet();

    /**
     * Check if PVP is enabled between the participants of this relationship.
     *
     * @return Whether pvp is enabled between the married players.
     */
    boolean isPVPEnabled();

    /**
     * Set whether pvp is enabled between participants of this relationship.
     *
     * @param pvpEnabled Whether pvp is enabled between the married players.
     */
    void setPVPEnabled(boolean pvpEnabled);
}
