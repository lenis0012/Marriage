package me.lenis0012.mr.children.thinking;

import me.lenis0012.mr.children.Child;
import net.minecraft.server.v1_4_R1.Entity;
import net.minecraft.server.v1_4_R1.EntityHuman;
import net.minecraft.server.v1_4_R1.EntityLiving;
import net.minecraft.server.v1_4_R1.EntityPlayer;

public class LookAtClosestCell implements BrainCell {
	private Class<? extends EntityLiving> type;
	private Child child;
	private EntityLiving entity;
	private int counter = 0;
	
	public LookAtClosestCell(Child child, Class<? extends EntityLiving> type) {
		this.type = type;
		this.child = child;
		this.entity = child.getHandle();
	}
	
	@Override
	public void onUpdate() {
		if(counter >= 20) {
			this.lookAt();
			counter = 0;
		} else
			counter++;
	}

	@Override
	public void onRemove() {}

	@Override
	public void onCreate() {
		this.lookAt();
	}

	@Override
	public String getType() {
		return "LookAtClosest";
	}
	
	public void lookAt() {
		if(!child.isStaying())
			return;
		
		Entity target = getClosestEntity(type, 10);
		entity.getControllerLook().a(target, 10, entity.bp());
	}
	
	public Entity getClosestEntity(Class<? extends EntityLiving> type, double distance) {
		Entity e = null;
		if(type == EntityHuman.class || type == EntityPlayer.class)
			e = entity.world.findNearbyPlayer(entity, distance);
		else
			e = entity.world.a(type, entity.boundingBox.grow(distance, 3, distance), entity);
		return e;
	}
	
	
	@Override
	public boolean canContinue() {
		return true;
	}
}
