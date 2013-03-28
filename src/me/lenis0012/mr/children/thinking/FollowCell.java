package me.lenis0012.mr.children.thinking;

import org.bukkit.craftbukkit.v1_5_R2.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;

import me.lenis0012.mr.children.Child;
import me.lenis0012.mr.util.PositionUtil;
import net.minecraft.server.v1_5_R2.EntityLiving;

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
		
		PositionUtil.move(entity, target, child.getSpeed());
	}

	@Override
	public void onRemove() {
		if(entity.getNavigation().f())
			entity.getNavigation().g();
	}

	@Override
	public void onCreate() {}
	
	public String getType() {
		return "Follow";
	}
	
	@Override
	public boolean canContinue() {
		return true;
	}
}
