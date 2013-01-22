package me.lenis0012.mr.events;

import me.lenis0012.mr.children.Child;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ChildDeathEvent extends Event {
	private final HandlerList handlers = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	private Child child;
	
	public ChildDeathEvent(Child child) {
		this.child = child;
	}
	
	public Child getChild() {
		return this.child;
	}
}
