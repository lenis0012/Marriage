package me.lenis0012.mr.events;

import me.lenis0012.mr.children.Child;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerInteractChildEvent extends Event implements Cancellable {
	private boolean canceled = false;
	private static final HandlerList handlers = new HandlerList();
	
	@Override
	public boolean isCancelled() {
		return this.canceled;
	}

	@Override
	public void setCancelled(boolean value) {
		this.canceled = value;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	private Child child;
	private Player player;
	
	public PlayerInteractChildEvent(Child child, Player player) {
		this.child = child;
		this.player = player;
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public Child getChild() {
		return this.child;
	}
}
