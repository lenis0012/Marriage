package me.lenis0012.mr.children;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Set;

import me.lenis0012.mr.Marriage;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ChildConfiguration {
	private static File dataFile = null;
	private static FileConfiguration data = null;
	
	public static void save(Child child) {
		check();
		if(child.getBukkitEnitity() == null)
			return;
		
		String id = String.valueOf(child.getID());
		Location l = child.getLocation();
		Calendar c = Calendar.getInstance();
		String date = c.get(Calendar.DAY_OF_YEAR)+"-"+c.get(Calendar.YEAR);
		String world = l.getWorld().getName();
		double x = l.getX();
		double y = l.getY();
		double z = l.getZ();
		float yaw = l.getYaw();
		float pitch = l.getPitch();
		data.set("child."+id+".location.world", world);
		data.set("child."+id+".location.x", x);
		data.set("child."+id+".location.y", y);
		data.set("child."+id+".location.z", z);
		data.set("child."+id+".location.yaw", yaw);
		data.set("child."+id+".location.pitch", pitch);
		data.set("child."+id+".owner", child.getParent());
		data.set("child."+id+".baby", child.isBaby());
		data.set("child."+id+".date", date);
		save();
	}
	
	public static ChildInfo getChild(int id) {
		check();
		
		String w = (String)getLocationVariable(id, "world");
		World world = Bukkit.getWorld(w);
		double x = (Double)getLocationVariable(id, "x");
		double y = (Double)getLocationVariable(id, "y");
		double z = (Double)getLocationVariable(id, "z");
		float yaw = (float) ((double)((Double)getLocationVariable(id, "yaw")));
		float pitch = (float) ((double)((Double)getLocationVariable(id, "pitch")));
		Location loc = new Location(world, x, y, z, yaw, pitch);
		boolean baby = (Boolean)getVariable(id, "baby");
		String owner = (String)getVariable(id, "owner");
		
		return new ChildInfo(loc, baby, owner);
	}
	
	private static Object getVariable(int id, String var) {
		return data.get("child."+id+"."+var);
	}
	
	private static Object getLocationVariable(int id, String var) {
		return data.get("child."+id+".location."+var);
	}
	
	public static boolean hasChild(Owner owner) {
		String name = owner.getName();
		String partner = owner.getPartner();
		
		for(String id : getChildren()) {
			String check = (String)getVariable(Integer.valueOf(id), "owner");
			if(check.equalsIgnoreCase(name) || check.equalsIgnoreCase(partner))
				return true;
		}
		
		return false;
	}
	
	public static ChildInfo getChild(Owner owner) {
		String name = owner.getName();
		String partner = owner.getPartner();
		
		for(String id : getChildren()) {
			String check = (String)getVariable(Integer.valueOf(id), "owner");
			if(check.equalsIgnoreCase(name) || check.equalsIgnoreCase(partner))
				return getChild(Integer.valueOf(id));
		}
		
		return null;
	}
	
	public static int getFromOwner(String owner) {
		return 0;
	}
	
	public static void remove(Child child) {
		check();
		
		String id = String.valueOf(child.getID());
		data.set("child."+id, null);
		save();
	}
	
	public static int getNextfreeID() {
		check();
		
		Set<String> set = getChildren();
		if(set != null) {
			for(int i = 1; i >= 0; i++) {
				String id = String.valueOf(i);
				if(!set.contains(id))
					return i;
			}
		}
		return 1;
	}
	
	public static Set<String> getChildren() {
		check();
		
		if(data.getConfigurationSection("child") != null) {
			return data.getConfigurationSection("child").getKeys(false);
		}
		return null;
	}
	
	private static void check() {
		if(dataFile == null)
			checkFile();
		if(data == null)
			reload();
	}
	
	public static void checkFile() {
		Marriage plugin = Marriage.instance;
		File file = new File(plugin.getDataFolder(), "childs.yml");
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {}
		}
		dataFile = file;
	}
	
	public static void reload() {
		if(dataFile == null)
			checkFile();
		
		data = YamlConfiguration.loadConfiguration(dataFile);
	}
	
	public static void save() {
		check();
		try {
			data.save(dataFile);
		} catch (IOException e) {}
	}
	
	public static class ChildInfo {
		public Location pos;
		public boolean isBaby;
		public String owner;
		
		public ChildInfo(Location loc, boolean isBaby, String owner) {
			this.pos = loc;
			this.isBaby = isBaby;
			this.owner = owner;
		}
	}
}
