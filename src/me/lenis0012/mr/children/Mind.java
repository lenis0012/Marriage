package me.lenis0012.mr.children;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Villager;

public class Mind {
	public List<BrainCell> cells = new ArrayList<BrainCell>();
	private ChildControler child;
	private Villager villager = null;
	
	public Mind(ChildControler child) {
		this.child = child;
	}
	
	public void update() {
		if(child.isSpawned()) {
			if(villager == null)
				villager = (Villager)child.getBukkitEnitity();
			
			for(BrainCell cell : cells) {
				cell.onUpdate();
			}
			
			if(child.isBaby() && villager.isAdult()) {
					villager.setBaby();
			} else if(!child.isBaby() && !villager.isAdult())
				villager.setAdult();
			
			child.loc = child.getBukkitEnitity().getLocation();
		}
	}
	
	public void addBrainCell(BrainCell cell) {
		if(!cells.contains(cell)) {
			cells.add(cell);
			cell.onCreate();
		}
	}
	
	public void removeBrainCell(BrainCell cell) {
		if(cells.contains(cell)) {
			cells.remove(cell);
			cell.onRemove();
		}
	}
}
