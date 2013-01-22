package me.lenis0012.mr.children;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.lenis0012.mr.events.ChildDeathEvent;
import me.lenis0012.mr.events.PlayerInteractChildEvent;
import net.minecraft.server.v1_4_R1.EntityHuman;
import net.minecraft.server.v1_4_R1.EntityPlayer;
import net.minecraft.server.v1_4_R1.EntityVillager;
import net.minecraft.server.v1_4_R1.World;

public class EntityChild extends EntityVillager {
	Child child;
	
	public EntityChild(World world) {
		this(world, null);
	}
	
	public EntityChild(World world, Child child) {
		super(world);
		this.child = child;
	}
	
	//entity moving
	@Override
	public void move(double x, double y, double z) {
		if(child.isStaying())
			return;
		
		super.move(x, y, z);
	}
	
	//entity being pushed
	@Override
	public void g(double x, double y, double z) {
		if(child.isStaying())
			return;
		
		super.g(x, y, z);
	}
	
	//entity reponse on tick loop
	@Override
	public void j_() {
		super.j_();
		
		//update the childs mind
		child.getBrain().update();
	}
	
	//player interacting with entity
	@Override
	public boolean a(EntityHuman entity) {
		if(entity instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)entity;
			Player player = ep.getBukkitEntity();
			PlayerInteractChildEvent ev = new PlayerInteractChildEvent(child, player);
			Bukkit.getServer().getPluginManager().callEvent(ev);
			if(ev.isCancelled())
				return false;
		}
		return super.a(entity);
	}
	
	@Override
	public void die() {
		ChildDeathEvent ev = new ChildDeathEvent(child);
		Bukkit.getServer().getPluginManager().callEvent(ev);
		super.die();
	}
}
