package me.lenis0012.mr.children;

import org.bukkit.craftbukkit.v1_4_R1.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;

import net.minecraft.server.v1_4_R1.EntityCreature;
import net.minecraft.server.v1_4_R1.EntityLiving;
import net.minecraft.server.v1_4_R1.PathEntity;

public class FollowCell implements BrainCell {
	private Child child;
	private EntityLiving entity;
	private EntityLiving target;
	
	public FollowCell(Child child, LivingEntity entity) {
		this.child = child;
		this.entity = child.getHandle();
		this.target = ((CraftLivingEntity)entity).getHandle();
	}
	
	@Override
	public void onUpdate() {
		if(child.isStaying() || target == entity)
			return;
		
		if(!entity.getNavigation().a(target, child.getSpeed())) {
			this.follow();
		}
	}

	@Override
	public void onRemove() {
		if(entity.getNavigation().f())
			entity.getNavigation().g();
	}

	@Override
	public void onCreate() {
		if(child.isStaying() || target == entity)
			return;
		
		if(!entity.getNavigation().a(target, child.getSpeed())) {
			this.follow();
		}
	}
	
	private void follow() {
		PathEntity path = entity.world.findPath(entity, target, 20, true, false, false, true);
		((EntityCreature)entity).setPathEntity(path);
	}
}
