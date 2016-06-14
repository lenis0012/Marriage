package com.lenis0012.bukkit.marriage2.events;

import com.lenis0012.bukkit.marriage2.MPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * MPlayerMarryEvent.
 *
 * Called when two players are about to get married.
 */
public class PlayerMarryEvent extends Event implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();

    private MPlayer requesing;
    private MPlayer requested;
    private MPlayer priest;
    private boolean cancelled = false;

    public PlayerMarryEvent(MPlayer requesing, MPlayer requested, MPlayer priest) {
        super(false);
        this.requesing = requesing;
        this.requested = requested;
        this.priest = priest;
    }

    /**
     * Get the player that requested to marry the other player.
     *
     * @return Requesting player
     */
    public MPlayer getRequesing() {
        return requesing;
    }

    /**
     * Get the player that was requested to be married.
     *
     * @return Requested player
     */
    public MPlayer getRequested() {
        return requested;
    }

    /**
     * Get priest that created/initiated the marriage.
     * Note: null if {@link #isFromPriest() isFromPriest} false.
     *
     * @return True is performed by priest, false otherwise
     */
    public MPlayer getPriest() {
        return priest;
    }

    /**
     * Check whether or not this marriage was created by a priest.
     *
     * @return Created/initiated by a priest
     */
    public boolean isFromPriest() {
        return priest != null;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
