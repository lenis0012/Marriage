package com.lenis0012.bukkit.marriage.util;

import java.lang.reflect.Field;
import java.util.Random;

import net.minecraft.server.v1_8_R1.DataWatcher;
import net.minecraft.server.v1_8_R1.EntityPlayer;
import net.minecraft.server.v1_8_R1.MathHelper;
import net.minecraft.server.v1_8_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R1.PacketPlayOutEntityStatus;
import net.minecraft.server.v1_8_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R1.PacketPlayOutWorldParticles;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketUtil {
	private static final Random random = new Random();
	
	public static void createHearts(Player player, Location loc) {
		final EntityPlayer ep = ((CraftPlayer) player).getHandle();
		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles();
		writeFieldData(packet, "a", "heart"); //Particle type
		writeFieldData(packet, "b", (float) loc.getX()); //Location X
		writeFieldData(packet, "c", (float) loc.getY()); //Location Y
		writeFieldData(packet, "d", (float) loc.getZ()); //Location Z
		writeFieldData(packet, "e", 1.0F); //Randomness
		writeFieldData(packet, "f", 1.0F); //Randomness
		writeFieldData(packet, "g", 1.0F); //Randomness
		writeFieldData(packet, "h", 1.0F); //Particle speed
		writeFieldData(packet, "i", 200); //Particle count
		ep.playerConnection.sendPacket(packet);
	}
	
	//This somehow crashes the client eh...
	public static void createHeartsAndCrashClient(Player player, Location loc) {
		final DataWatcher tmp = new DataWatcher(null);
		final EntityPlayer ep = ((CraftPlayer) player).getHandle();
		final int entityId = Short.MAX_VALUE - 200 - random.nextInt(100);
		tmp.a(0, Byte.valueOf((byte) 32)); //32 - Invicibility id
		tmp.a(12, Integer.valueOf((int) 0)); //Adult :P
		
		PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving();
		writeFieldData(packet, "a", entityId);
		writeFieldData(packet, "b", 95);
		writeFieldData(packet, "c", MathHelper.floor(loc.getX() * 32.0D));
		writeFieldData(packet, "d", MathHelper.floor(loc.getY() * 32.0D));
		writeFieldData(packet, "e", MathHelper.floor(loc.getZ() * 32.0D));
		writeFieldData(packet, "f", 0);
		writeFieldData(packet, "g", 0);
		writeFieldData(packet, "h", 0);
		writeFieldData(packet, "i", getByteFromDegree(loc.getYaw()));
		writeFieldData(packet, "j", getByteFromDegree(loc.getPitch()));
		writeFieldData(packet, "k", getByteFromDegree(loc.getYaw()));
		writeFieldData(packet, "l", tmp);
		
		//Send spawn packet
		ep.playerConnection.sendPacket(packet);
		
		PacketPlayOutEntityStatus packet2 = new PacketPlayOutEntityStatus();
		writeFieldData(packet, "a", entityId);
		writeFieldData(packet, "b", (byte) 7);
		
		//Send status packet
		ep.playerConnection.sendPacket(packet2);
		
		PacketPlayOutEntityDestroy packet3 = new PacketPlayOutEntityDestroy();
		writeFieldData(packet3, "a", new int[] {entityId});
		
		//Send destroy packet
		ep.playerConnection.sendPacket(packet3);
	}
	
	private static void writeFieldData(Object handle, String fname, Object fvalue) {
		try {
			Field field = getField(handle.getClass(), fname);
			field.setAccessible(true);
			field.set(handle, fvalue);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static Field getField(Class<?> clazz, String fname) throws Exception {
		if(clazz == null) {
			return null;
		}
		
		Field field = clazz.getDeclaredField(fname);
		if(field != null) {
			return field;
		} else {
			return getField(clazz.getSuperclass(), fname);
		}
	}
	
	/*
	 * From CraftBukkit - Packet24MobSpawn.java
	 * line 31 - 33
	 * 
	 * All credits go to CraftBukkit
	 */
	private static byte getByteFromDegree(float degree) {
		return (byte) (int)(degree * 256.0F / 360.0F);
	}
}
