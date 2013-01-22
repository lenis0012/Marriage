package me.lenis0012.mr.children.thinking;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.lenis0012.mr.children.ChildControler;

import org.bukkit.entity.Villager;

public class Brain {
	private List<BrainCell> cells = new ArrayList<BrainCell>();
	private ChildControler child;
	private Villager villager = null;
	
	public Brain(ChildControler child) {
		this.child = child;
	}
	
	public void update() {
		if(child.isSpawned()) {
			if(villager == null)
				villager = (Villager)child.getBukkitEnitity();
			
			Iterator<BrainCell> it = cells.iterator();
			while(it.hasNext()) {
				BrainCell cell = it.next();
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
	
	public List<BrainCell> getCells() {
		return this.cells;
	}
	
	public void removeBrainCell(BrainCell cell) {
		Iterator<BrainCell> it = cells.iterator();
		while(it.hasNext()) {
			BrainCell c = it.next();
			if(cell == c)
				it.remove();
		}
	}
	
	public void removeBrainCellWithType(String type) {
		Iterator<BrainCell> it = cells.iterator();
		while(it.hasNext()) {
			BrainCell c = it.next();
			if(c.getType().equalsIgnoreCase(type))
				it.remove();
		}
	}
}
