package me.lenis0012.mr;

import java.util.List;

import net.minecraft.server.v1_5_R1.EntityPlayer;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_5_R1.CraftServer;
import org.bukkit.craftbukkit.v1_5_R1.entity.CraftPlayer;

public class SimpleMPlayer extends CraftPlayer implements MPlayer {
	private String name;
	private FileConfiguration cfg;
	private Marriage plugin;
	private boolean chatting = false;
	
	public SimpleMPlayer(CraftServer server, EntityPlayer entity) {
		super(server, entity);
		plugin = Marriage.instance;
		this.name = this.getName();
		this.cfg = plugin.getCustomConfig();
	}
	
	public boolean isMarried() {
		String par1Str = cfg.getString("Married." + name);
		return par1Str != null && par1Str != "";
	}
	
	public String getPartner() {
		if(isMarried())
		{
			String par1Str = cfg.getString("Married." + name);
			return par1Str;
		}
		return "";
	}
	
	public void setPartner(String user) {
		List<String> list =  cfg.getStringList("partners");
		list.add(user);
		cfg.set("Married." + name, user);
		cfg.set("Married." + user, name);
		cfg.set("partners", list);
		save();
	}
	
	public void divorce() {
		if(this.isMarried()) {
			String partner = this.getPartner();
			List<String> list =  cfg.getStringList("partners");
			if(list.contains(name))
				list.remove(name);
			if(list.contains(partner))
				list.remove(partner);
			cfg.set("Married."+name, null);
			cfg.set("Married."+partner, null);
			cfg.set("home."+name, null);
			cfg.set("home."+partner, null);
			cfg.set("partners", list);
			save();
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
}
