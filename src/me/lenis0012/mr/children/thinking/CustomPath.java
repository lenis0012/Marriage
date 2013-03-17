package me.lenis0012.mr.children.thinking;

import org.bukkit.craftbukkit.v1_5_R1.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;

import net.minecraft.server.v1_5_R1.EntityLiving;
import net.minecraft.server.v1_5_R1.PathEntity;

public class CustomPath {
	
	/**
	 * get the entity path
	 * 
	 * @param entity entity
	 * @param target target
	 * @return Entity path
	 */
	public static PathEntity createPath(EntityLiving entity, EntityLiving target) {
		return entity.world.findPath(entity, target, 20, true, false, false, true);
	}
	
	/**
	 * get the entity path
	 * 
	 * @param entity entity
	 * @param target target
	 * @return Entity path
	 * @Deprecated  Method broken
	 */
	@Deprecated
	public static PathEntity createPath(LivingEntity entity, LivingEntity target) {
		EntityLiving e1 = ((CraftLivingEntity)entity).getHandle();
		EntityLiving e2 = ((CraftLivingEntity)target).getHandle();
		return createPath(e1, e2);
	}
	
	/**
	 * get the entity path
	 * 
	 * @param entity entity
	 * @param target target
	 * @return Entity path
	 */
	public static PathEntity createPath(EntityLiving entity, int x, int y, int z) {
		return entity.world.a(entity, x, y, z, 20, true, false, false, true);
	}
	
	/**
	 * get the entity path
	 * 
	 * @param entity entity
	 * @param target target
	 * @return Entity path
	 * @Deprecated  Method broken
	 */
	@Deprecated
	public static PathEntity createPath(LivingEntity entity, int x, int y, int z) {
		EntityLiving e1 = ((CraftLivingEntity)entity).getHandle();
		return createPath(e1, x, y, z);
	}
}
