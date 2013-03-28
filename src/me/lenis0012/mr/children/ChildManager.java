package me.lenis0012.mr.children;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.lenis0012.mr.children.ChildConfiguration.ChildInfo;
import me.lenis0012.mr.util.ReflectionUtil;
import net.minecraft.server.v1_5_R2.EntityPlayer;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.craftbukkit.v1_5_R2.CraftServer;
import org.bukkit.craftbukkit.v1_5_R2.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ChildManager {
	private JavaPlugin plugin;
	private static ChildManager instance;
	public static List<Child> children = new ArrayList<Child>();
	public HashMap<String, Child> parents = new HashMap<String, Child>();
	
	public ChildManager(JavaPlugin plugin) {
		this.plugin = plugin;
		this.start();
		instance = this;
	}
	
	public void start() {
		plugin.getCommand("child").setExecutor(new ChildCommand());
		plugin.getServer().getPluginManager().registerEvents(new Listener(), plugin);
		this.registerChildren();
	}
	
	public void stop() {
		for(Child child : children) {
			child.save();
			if(child.isSpawned())
				child.deSpawn(true);
		}
	}
	
	public static ChildManager getInstance() {
		return instance;
	}
	
	public Owner getOwner(Player player) {
		CraftPlayer cp = (CraftPlayer)player;
		EntityPlayer ep = cp.getHandle();
		CraftServer cs = (CraftServer)Bukkit.getServer();
		
		return new IOwner(cs, ep);
	}
	
	public void registerChildren() {
		Class<?> cbClass = EntityChild.class;
		String cbType = "Villager";
		int cbID = EntityType.VILLAGER.getTypeId();
		ReflectionUtil.registerEntityType(cbClass, cbType, cbID);
	}
	
	public Child loadChild(Owner owner) {
		String name = owner.getName();
		ChildInfo info = ChildConfiguration.getChild(ChildConfiguration.getFromOwner(name));
		
		return loadChild(info);
	}
	
	public Child loadChild(ChildInfo info) {
		Child child = this.createChild(info.owner);
		child.spawn(info.pos, true);
		child.setBaby(info.isBaby);
		
		return child;
	}
	
	private int getNextFree() {
		return ChildConfiguration.getNextfreeID();
	}
	
	public Child createChild(String owner) {
		Child child = new ChildControler(this.getNextFree(), owner);
		children.add(child);
		parents.put(owner, child);
		
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
					child.spawn(child.getLocation(), false);
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
		
		@EventHandler
		public void onPlayerJoin(PlayerJoinEvent event) {
			Player player = event.getPlayer();
			ChildManager manager = ChildManager.getInstance();
			Owner owner = manager.getOwner(player);
			if(owner.hasChild()) {
				Child child = owner.getChild();
				if(child != null) {
					manager.parents.put(player.getName(), child);
					if(!child.isSpawned()) {
						child.spawn(player.getLocation(), true);
					}
				}
			}
		}
	}
}
