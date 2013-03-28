package me.lenis0012.mr.child;

import java.util.HashMap;
import java.util.Map;

import me.lenis0012.mr.Marriage;

public class ChildManager {
	private Marriage plugin;
	private Map<ParentPair, Child> children = new HashMap<ParentPair, Child>();
	
	public ChildManager(Marriage plugin) {
		this.plugin = plugin;
	}
	
}