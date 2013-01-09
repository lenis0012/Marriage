package me.lenis0012.mr.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_4_6.Entity;
import net.minecraft.server.v1_4_6.EntityHuman;
import net.minecraft.server.v1_4_6.EntityLiving;
import net.minecraft.server.v1_4_6.EntityPlayer;

public class EntityUtil {
	/*
	 * This class is based of reflection
	 * It does functions bukkit's api cant do
	 * All credits @Mojang
	 * 
	 * P.S, Do NOT use this code for a marriage plugin.
	 * Unless you have my permission.
	 */
	
	public static EntityLiving getEntityLivingFromEntity(org.bukkit.entity.Entity entity) {
		EntityLiving el = (EntityLiving)entity;
		return el;
	}
	
	public static Entity getEntityFromEntity(org.bukkit.entity.Entity entity) {
		Entity e = (Entity)entity;
		return e;
	}
	
	public static void lookAt(EntityLiving entity, Entity target) {
		entity.getControllerLook().a(target, 10, entity.bp());
	}
	
	public static void lookAt(EntityLiving entity, Location loc) {
		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();
		float yaw = loc.getYaw();
		float pitch = loc.getPitch();
		entity.getControllerLook().a(x, y, z, yaw, pitch);
	}
	
	public static org.bukkit.entity.Entity getBukkitEntityFromEntity(Entity from) {
		return (org.bukkit.entity.Entity)from;
	}
	
	public static org.bukkit.entity.Entity getBukkitEntityFromEntityLiving(EntityLiving from) {
		return (org.bukkit.entity.Entity)from;
	}
	
	public static Entity getClosestEntity(Entity entity, Class<? extends EntityLiving> type, double distance) {
		Entity e = null;
		if(type == EntityHuman.class || type == EntityPlayer.class) {
			e = entity.world.findNearbyPlayer(entity, distance);
		}else {
			e = entity.world.a(type, entity.boundingBox.grow(distance, 3, distance), entity);
		}
		return e;
	}
	
	public static org.bukkit.entity.Entity getEntityFromView(Player player) {
		return MathUtil.getEntityFromView(player);
	}
	
	public static org.bukkit.entity.Entity getEntityFromView(org.bukkit.entity.Entity entity) {
		return MathUtil.getEntityFromView(entity);
	}
}
