package me.lenis0012.mr.children;

import me.lenis0012.mr.MPlayer;

public interface Owner extends MPlayer {
	public boolean hasChild();
	
	public Child getChild();
	
	public void NotifyChildDeath();
}
