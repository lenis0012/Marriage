package me.lenis0012.mr;

import org.bukkit.entity.Player;

public interface MPlayer extends Player {
	public boolean isMarried();
	
	public String getPartner();
	
	public void setPartner(String user);
	
	public void divorce();
}
