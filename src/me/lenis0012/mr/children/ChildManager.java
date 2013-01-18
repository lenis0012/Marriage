package me.lenis0012.mr.children;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.reflection.classes.EntityTypesRef;

public class ChildManager {
	private JavaPlugin plugin;
	private boolean added = false;
	private static ChildManager instance;
	public static List<Child> children = new ArrayList<Child>();
	public HashMap<String, Child> parents = new HashMap<String, Child>();
	
	public ChildManager(JavaPlugin plugin) {
		this.plugin = plugin;
		this.start();
	}
	
	public void start() {
		plugin.getCommand("child").setExecutor(new ChildCommand());
		plugin.getServer().getPluginManager().registerEvents(new Listener(), plugin);
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
		parents.put(owner.getName(), child);
		return child;
	}
	
	private static class Listener implements org.bukkit.event.Listener {
		private HashMap<Chunk, List<Child>> toRender = new HashMap<Chunk, List<Child>>();
		
		@EventHandler
		public void onLoad(ChunkLoadEvent event) {
			Chunk c = event.getChunk();
			
			if(toRender.containsKey(c)) {
				List<Child> list = toRender.get(c);
				for(Child child : list) {
					child.spawn(child.getLocation());
					list.remove(child);
				}
				toRender.put(c, list);
			}
		}
		
		@EventHandler (priority = EventPriority.MONITOR)
		public void onChunkUnload(ChunkUnloadEvent event) {
			if(event.isCancelled())
				return;
			
			Chunk c = event.getChunk();
			
			List<Child> list = new ArrayList<Child>();
			if(toRender.containsKey(c))
				list = toRender.get(c);
			
			for(Child child : children) {
				Chunk cc = child.getBukkitEnitity().getLocation().getChunk();
				if(c.getX() == cc.getX() && c.getZ() == cc.getZ()) {
					list.add(child);
				}
			}
			if(!list.isEmpty())
				toRender.put(c, list);
			else if(toRender.containsKey(c)) {
				toRender.remove(c);
			}
		}
	}
}
