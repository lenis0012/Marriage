package me.lenis0012.mr.children.thinking;

import org.bukkit.craftbukkit.v1_4_R1.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;

import net.minecraft.server.v1_4_R1.EntityLiving;
import net.minecraft.server.v1_4_R1.PathEntity;

public class CustomPath {
	
	public static PathEntity createPath(EntityLiving entity, EntityLiving target) {
		return entity.world.findPath(entity, target, 20, true, false, false, true);
	}
	
	public static PathEntity createPath(LivingEntity entity, LivingEntity target) {
		EntityLiving e1 = ((CraftLivingEntity)entity).getHandle();
		EntityLiving e2 = ((CraftLivingEntity)target).getHandle();
		return createPath(e1, e2);
	}
}
