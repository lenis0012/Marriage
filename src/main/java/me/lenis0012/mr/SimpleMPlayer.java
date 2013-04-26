package me.lenis0012.mr;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public class SimpleMPlayer implements MPlayer {
	private String name;
	private Marriage plugin;
	private boolean chatting = false;
	private FileConfiguration cfg;
	
	public SimpleMPlayer(String name) {
		plugin = Marriage.instance;
		this.name = name;
		this.cfg = plugin.getCustomConfig();
	}
	
	public boolean isMarried() {
		String par1Str = getConfig().getString("partner");
		return par1Str != null && par1Str != "";
	}
	
	public String getPartner() {
		if(isMarried()) {
			String par1Str = getConfig().getString("partner");
			return par1Str;
		}
		return "";
	}
	
	public void setPartner(String user) {
		List<String> list = cfg.getStringList("partners");
		list.add(user);
		PlayerConfig cfg = this.getConfig();
		PlayerConfig partner_cfg = plugin.getConfig(user);
		cfg.set("partner", user);
		partner_cfg.set("partner", name);
		plugin.getCustomConfig().set("partners", list);
		save();
		cfg.save();
		partner_cfg.save();
	}
	
	public void divorce() {
		if(this.isMarried()) {
			String partner = this.getPartner();
			List<String> list =  cfg.getStringList("partners");
			if(list.contains(name))
				list.remove(name);
			if(list.contains(partner))
				list.remove(partner);
			
			PlayerConfig cfg = this.getConfig();
			PlayerConfig partner_cfg = plugin.getConfig(getPartner());
			cfg.set("partner", null);
			cfg.set("home", null);
			partner_cfg.set("partner", null);
			partner_cfg.set("home", null);
			plugin.getCustomConfig().set("partners", list);
			save();
			cfg.save();
			partner_cfg.save();
		}
	}
	
	private void save() {
		plugin.saveCustomConfig();
	}

	@Override
	public void setChatting(boolean value) {
		this.chatting = value;
	}

	@Override
	public boolean isChatting() {
		return chatting;
	}

	@Override
	public void setHome(Location loc) {
		PlayerConfig cfg = this.getConfig();
		PlayerConfig partner_cfg = plugin.getConfig(getPartner());
		String world = loc.getWorld().getName();
		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();
		float yaw = loc.getYaw();
		float pitch = loc.getPitch();
		cfg.set("home.world", world);
		cfg.set("home.x", x);
		cfg.set("home.y", y);
		cfg.set("home.z", z);
		cfg.set("home.yaw", yaw);
		cfg.set("home.pitch", pitch);
		partner_cfg.set("home.world", world);
		partner_cfg.set("home.x", x);
		partner_cfg.set("home.y", y);
		partner_cfg.set("home.z", z);
		partner_cfg.set("home.yaw", yaw);
		partner_cfg.set("home.pitch", pitch);
		cfg.save();
		partner_cfg.save();
	}

	@Override
	public Location getHome() {
		PlayerConfig cfg = this.getConfig();
		if(!cfg.contains("home"))
			return null;
		
		World world = Bukkit.getWorld(cfg.getString("home.world"));
		double x = cfg.getDouble("home.x");
		double y = cfg.getDouble("home.y");
		double z = cfg.getDouble("home.z");
		float yaw = cfg.getInt("home.yaw");
		float pitch = cfg.getInt("home.pitch");
		
		if(world != null)
			return new Location(world, x, y, z, yaw, pitch);
		else
			return null;
	}

	@Override
	public PlayerConfig getConfig() {
		return plugin.getConfig(name);
	}
}