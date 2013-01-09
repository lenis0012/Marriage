package me.lenis0012.mr.util;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class MathUtil {
	public static Entity getEntityFromView(Entity entity) {
		Entity e = null;
		double a = 0;
		double b = 1;
		Vector c = entity.getLocation().toVector(); //gets the players location in a vector
		Vector d = entity.getLocation().getDirection().normalize(); //returns the direction of the player
		double p = Math.cos(Math.PI / 4); //PI is the surface of a round
		for(LivingEntity check : entity.getWorld().getEntitiesByClass(LivingEntity.class)) { //loops all living entities in the world
			if(check == entity)
				continue; //we dont want the loop to return the entity itself
			if(e == null || a > check.getLocation().distanceSquared(entity.getLocation())) {
				Vector sub = check.getLocation().add(0, 1, 0).toVector().subtract(c); //gets the substract vector between the checking entity and sended entity
				if(d.clone().crossProduct(sub).lengthSquared() < b && sub.normalize().dot(d) >= p) { //check if the entity is closer then the last checked entity to the player and check if it is looking at the checked entity
					e = check; //set the returnign entity to the checked entity
					a = check.getLocation().distanceSquared(entity.getLocation()); //update the last ditance between the sended and cheked entity
				}
			}
		}
		return e;
	}
	
	public static Entity getEntityFromView(Player entity) {
		Entity e = null;
		double a = 0;
		double b = 1;
		Vector c = entity.getLocation().toVector(); //gets the players location in a vector
		Vector d = entity.getLocation().getDirection().normalize(); //returns the direction of the player
		double p = Math.cos(Math.PI / 4); //PI is the surface of a round
		for(LivingEntity check : entity.getWorld().getEntitiesByClass(LivingEntity.class)) { //loops all living entities in the world
			if(check == entity)
				continue; //we dont want the loop to return the entity itself
			if(e == null || a > check.getLocation().distanceSquared(entity.getLocation())) {
				Vector sub = check.getLocation().add(0, 1, 0).toVector().subtract(c); //gets the substract vector between the checking entity and sended entity
				if(d.clone().crossProduct(sub).lengthSquared() < b && sub.normalize().dot(d) >= p) { //check if the entity is closer then the last checked entity to the player and check if it is looking at the checked entity
					e = check; //set the returnign entity to the checked entity
					a = check.getLocation().distanceSquared(entity.getLocation()); //update the last ditance between the sended and cheked entity
				}
			}
		}
		return e;
	}
}
