package me.lenis0012.mr.util;

import org.bukkit.craftbukkit.v1_5_R2.entity.CraftOcelot;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class PacketUtil {
	
	public static void createHearts(Entity entity, Player player) {
		//TODO: Spawn an invissible ocelot?
		//Spawn an ocelot
		CraftOcelot ocelot = (CraftOcelot)entity.getWorld().spawnEntity(entity.getLocation(), EntityType.OCELOT);
		//Send a Packet38EntityStatus with the effect id 7 (Wolf tamed)
		ocelot.getHandle().world.broadcastEntityEffect(ocelot.getHandle(), (byte) 7);
		//Remove the ocelot
		ocelot.remove();
	}
}