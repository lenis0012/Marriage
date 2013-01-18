package me.lenis0012.mr.children;

import java.util.ArrayList;
import java.util.List;

public class Mind {
	public List<BrainCell> cells = new ArrayList<BrainCell>();
	
	public void update() {
		for(BrainCell cell : cells) {
			cell.onUpdate();
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
