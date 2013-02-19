package me.lenis0012.mr.children.thinking;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.lenis0012.mr.children.ChildControler;

import org.bukkit.entity.Villager;

@SuppressWarnings("unused")
public class Brain {
	private List<BrainCell> cells = new ArrayList<BrainCell>();
	private ChildControler child;
	private Villager villager = null;
	private boolean ct = false;
	private int counter = 0;
	
	public Brain(ChildControler child) {
		this.child = child;
	}
	
	public void update() {
		if(counter >= 6) {
			if(child.isSpawned()) {
				if(villager == null)
					villager = (Villager)child.getBukkitEnitity();
				
				Iterator<BrainCell> it = cells.iterator();
				while(it.hasNext()) {
					BrainCell cell = it.next();
					if(cell.canContinue())
						cell.onUpdate();
					else
						it.remove();
				}
				
				/*long time = child.getBukkitEnitity().getWorld().getTime();
				
				if(child.getHandle().ticksLived > 15) {
					if(time >= 5000 && time <= 7000 && !ct) {
						this.addBrainCell(new AvoidSunCell(child));
						ct = true;
					}else if(!(time >= 5000 && time <= 7000) && ct) {
						this.removeBrainCellWithType("avoidsun");
						ct = false;
					}
				}*/
				
				if(child.isBaby() && villager.isAdult())
					villager.setBaby();
				else if(!child.isBaby() && !villager.isAdult())
					villager.setAdult();
				
				child.loc = child.getBukkitEnitity().getLocation();
			}
			counter = 0;
		} else
			counter ++;
	}
	
	public void addBrainCell(BrainCell cell) {
		if(!cells.contains(cell)) {
			cell.onCreate();
			cells.add(cell);
		}
	}
	
	public List<BrainCell> getCells() {
		return this.cells;
	}
	
	public void removeBrainCell(BrainCell cell) {
		Iterator<BrainCell> it = cells.iterator();
		while(it.hasNext()) {
			BrainCell c = it.next();
			if(cell == c) {
				cell.onRemove();
				it.remove();
			}
		}
	}
	
	public void removeBrainCellWithType(String type) {
		Iterator<BrainCell> it = cells.iterator();
		while(it.hasNext()) {
			BrainCell c = it.next();
			if(c.getType().equalsIgnoreCase(type)) {
				c.onRemove();
				it.remove();
			}
		}
	}
	
	public void removeAllBrainCells() {
		Iterator<BrainCell> it = cells.iterator();
		while(it.hasNext()) {
			it.next();
			it.remove();
		}
	}
}
