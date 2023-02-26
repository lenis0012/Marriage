package com.lenis0012.bukkit.marriage2.events;

import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.Relationship;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Player divorce event.
 * <p>
 * Called when two players are about to divorce.
 */
public class PlayerDivorceEvent extends Event implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();

    private MPlayer player;
    private Relationship marriage;

    public PlayerDivorceEvent(MPlayer player, Relationship marriage) {
        super(false);
        this.player = player;
        this.marriage = marriage;
    }

    /**
     * Get the player that initiated the divorce.
     *
     * @return Divorcing player
     */
    public MPlayer getPlayer() {
        return player;
    }

    /**
     * Get the marriage that is about to end.
     *
     * @return Marriage
     */
    public Relationship getMarriage() {
        return marriage;
    }

    private boolean cancelled = false;

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
