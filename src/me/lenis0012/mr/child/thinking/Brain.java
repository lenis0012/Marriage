package me.lenis0012.mr.child.thinking;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Brain {
	private int update_delay = 3;
	List<BrainCell> cells = new ArrayList<BrainCell>();
	
	public void update() {
		if(update_delay >= 1) {
			update_delay--;
			return;
		} else
			update_delay = 3;
		
		synchronized(cells) {
			Iterator<BrainCell> it = cells.iterator();
			while(it.hasNext()) {
				BrainCell cell = it.next();
				if(cell.canExecute()) {
					cell.onUpdate();
				}
			}
		}
	}
	
	public void addCell(BrainCell cell) {
		synchronized(cells) {
			cell.onCreate();
			cells.add(cell);
		}
	}
	
	public void removeCell(BrainCell cell) {
		synchronized(cells) {
			cell.onRemove();
			cells.remove(cell);
		}
	}
	
	public void removeCell(String type) {
		synchronized(cells) {
			Iterator<BrainCell> it = cells.iterator();
			while(it.hasNext()) {
				BrainCell cell = it.next();
				if(cell.getType().equalsIgnoreCase(type)) {
					cell.onRemove();
					it.remove();
				}
			}
		}
	}
	
	public void removeAllCells() {
		synchronized(cells) {
			Iterator<BrainCell> it = cells.iterator();
			while(it.hasNext()) {
				BrainCell cell = it.next();
				cell.onRemove();
				it.remove();
			}
		}
	}
}