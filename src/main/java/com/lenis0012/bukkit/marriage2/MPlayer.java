package com.lenis0012.bukkit.marriage2;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Representation of a player in Marriage.
 * This class is used to store data about a player, and managing its relationship(s).
 */
public interface MPlayer {

    /**
     * Get unique User Id of the player.
     *
     * @return Player's unique user id.
     */
    UUID getUniqueId();

    /**
     * Request marriage with this player by another.
     *
     * @param from Player who requested the marriage.
     */
    void requestMarriage(UUID from);

    /**
     * Check if marriage with this player is requested by another.
     *
     * @param from Marriage requester.
     * @return Whether marriage is requested by the player.
     */
    boolean isMarriageRequested(UUID from);

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
     * @deprecated Use
     */
    //     * @deprecated Use {@link #getFirstMarriage()} or {@link #getMarriage(UUID)} or {@link #getMarriages()}
//    @Deprecated
    @Nullable
    MData getMarriage();

    /**
     * Retrieve the (first) current active relationship of the player.
     *
     * @return Active relationship, null if not married.
     */
    @Nullable
    Relationship getActiveRelationship();

    /**
     * Get the last name the player logged on with.
     *
     * @return Last name, can be null
     */
    @Nullable
    String getLastName();

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
     * Whether the player is a priest.
     *
     * @return True if player ius priest, false otherwise
     */
    boolean isPriest();

    /**
     * Set the last name the player logged on with.
     *
     * @param name of player
     */
    void setLastName(String name);

    /**
     * Set whether this player is a priest.
     *
     * @param priest True if player is priest, false otherwise
     */
    void setPriest(boolean priest);

    /**
     * Set if the player is in marry chat-mode.
     *
     * @param inChat Whether the player is in marry chat-mode.
     */
    void setInChat(boolean inChat);

    /**
     * Get the current player's partner
     *
     * @return Current partner of the player
     * @deprecated Use {@link #getActiveRelationship()} instead
     */
    @Deprecated
    MPlayer getPartner();

    /**
     * Divorce with the current player's partner
     */
    @Deprecated
    void divorce();

    /**
     * Get timestamp of last time player logged in.
     *
     * @return Last login timestamp.
     */
    long getLastLogin();

    /**
     * Get timestamp of last time player logged out.
     *
     * @return Last logout timestamp.
     */
    long getLastLogout();

    /**
     * Said whether player is spying on marriage private chat.
     *
     * @param enabled True if enabled, false otherwise
     */
    void setChatSpy(boolean enabled);

    /**
     * Get whether player is spying on marriage private chat.
     *
     * @return True if enabled, false otherwise
     */
    boolean isChatSpy();
}