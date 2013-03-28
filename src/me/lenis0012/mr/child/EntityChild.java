package me.lenis0012.mr.child;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.lenis0012.mr.Marriage;
import me.lenis0012.mr.events.ChildDeathEvent;
import me.lenis0012.mr.events.PlayerInteractChildEvent;
import net.minecraft.server.v1_5_R2.EntityHuman;
import net.minecraft.server.v1_5_R2.EntityPlayer;
import net.minecraft.server.v1_5_R2.EntityVillager;
import net.minecraft.server.v1_5_R2.World;

public class EntityChild extends EntityVillager {
	private Child child;
	
	public EntityChild(World world) {
		super(world);
		Bukkit.getServer().getScheduler().runTask(Marriage.instance, new Runnable() {

			@Override
			public void run() {
				getBukkitEntity().remove();
			}
			
		});
	}
	
	public EntityChild(World world, Child child) {
		super(world);
		this.child = child;
	}
	
	@Override
	public void move(double x, double y, double z) {
		if(child.isStaying())
			return;
		
		super.move(x, y, z);
	}
	
	@Override
	public void g(double x, double y, double z) {
		if(child.isStaying())
			return;
		
		super.g(x, y, z);
	}
	
	@Override
	public void l_() {
		if(this.ticksLived > 20)
			this.ticksLived--;
		super.l_();
		
		child.getBrain().update();
	}
	
	@Override
	public boolean a_(EntityHuman entity) {
		if(entity instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)entity;
			Player player = ep.getBukkitEntity();
			PlayerInteractChildEvent ev = new PlayerInteractChildEvent(/*child*/ null, player);
			Bukkit.getServer().getPluginManager().callEvent(ev);
			if(ev.isCancelled())
				return false;
		}
		return super.a_(entity);
	}
	
	@Override
	public void die() {
		ChildDeathEvent ev = new ChildDeathEvent(/*child*/ null);
		Bukkit.getServer().getPluginManager().callEvent(ev);
		child.despawn(true);
	}
}