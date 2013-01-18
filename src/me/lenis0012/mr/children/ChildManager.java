package me.lenis0012.mr.children;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.reflection.classes.EntityTypesRef;

public class ChildManager {
	private JavaPlugin plugin;
	private boolean added = false;
	private static ChildManager instance;
	public List<Child> children = new ArrayList<Child>();
	
	public ChildManager(JavaPlugin plugin) {
		this.plugin = plugin;
		this.start();
	}
	
	public void start() {
		plugin.getCommand("child").setExecutor(new ChildCommand());
		this.registerChildren();
	}
	
	public void setInstance(ChildManager manager) {
		instance = manager;
	}
	
	public static ChildManager getInstance() {
		return instance;
	}
	
	public void registerChildren() {
		if(!added) {
			try {
				Class<?> cbClass = EntityChild.class;
				String cbType = "Villager";
				int cbID = EntityType.VILLAGER.getTypeId();
				
				//Old reflection system
				/*//data format start
				Class[] tmp = new Class[3];
				tmp[0] = Class.class;
				tmp[1] = String.class;
				tmp[2] = int.class;
				//data format end
				
				Method entities = net.minecraft.server.v1_4_R1.EntityTypes.class.getDeclaredMethod("a", tmp);
				
				//write custom data to the entity list
				entities.invoke(entities, cbClass, cbType, cbID);*/
				
				//new reflection system, from BKCommonLib
				EntityTypesRef.register(cbClass, cbType, cbID);
				this.added = true;
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public Child createChild(Player owner) {
		Child child = new ChildControler(1, owner);
		children.add(child);
		return child;
	}
}
