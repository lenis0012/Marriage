package com.lenis0012.bukkit.marriage.util;

//import java.lang.reflect.Field;
//import java.util.Random;
//import java.util.logging.Level;

//import net.minecraft.server.v1_6_R3.DataWatcher;
//import net.minecraft.server.v1_6_R3.EntityPlayer;
//import net.minecraft.server.v1_6_R3.MathHelper;
//import net.minecraft.server.v1_6_R3.Packet24MobSpawn;
//import net.minecraft.server.v1_6_R3.Packet29DestroyEntity;
//import net.minecraft.server.v1_6_R3.Packet38EntityStatus;

//import org.bukkit.Bukkit;
import org.bukkit.Location;
//import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketUtil {
//	private static final Random random = new Random();
	
	//Broken
	@Deprecated
	public static void createHearts(Player player, Location loc) {
		/*final DataWatcher tmp = new DataWatcher();
		final EntityPlayer ep = ((CraftPlayer) player).getHandle();
		final int entityId = Short.MAX_VALUE - 200 - random.nextInt(100);
		tmp.a(0, Byte.valueOf((byte) 32)); //32 - Invicibility id
		tmp.a(12, Integer.valueOf((int) 0)); //Adult :P
		
		Packet24MobSpawn packet = new Packet24MobSpawn();
		packet.a = entityId;
		packet.b = 95;
		packet.c = MathHelper.floor(loc.getX() * 32.0D);
		packet.d = MathHelper.floor(loc.getY() * 32.0D);
		packet.e = MathHelper.floor(loc.getZ() * 32.0D);
		packet.i = getByteFromDegree(loc.getYaw());
		packet.j = getByteFromDegree(loc.getPitch());
		packet.k = getByteFromDegree(loc.getYaw());
		packet.f = 0;
		packet.g = 0;
		packet.h = 0;
		
		Field field;
		try {
			field = Packet24MobSpawn.class.getDeclaredField("t");
			field.setAccessible(true);
			field.set(packet, tmp);
			field.setAccessible(false);
		} catch(Exception e) {
			Bukkit.getLogger().log(Level.SEVERE, "Failed to acces datawatcher field", e);
		}
		//Send spawn packet
		ep.playerConnection.sendPacket(packet);
		
		Packet38EntityStatus packet2 = new Packet38EntityStatus();
		packet2.a = entityId;
		packet2.b = (byte) 7;
		//Send status packet
		ep.playerConnection.sendPacket(packet2);
		
		Packet29DestroyEntity packet3 = new Packet29DestroyEntity();
		packet3.a = new int[] {entityId};
		//Send destroy packet
		ep.playerConnection.sendPacket(packet3);*/
	}
	
	/*
	 * From CraftBukkit - Packet24MobSpawn.java
	 * line 31 - 33
	 * 
	 * All credits go to CraftBukkit
	 */
	@SuppressWarnings("unused")
	private static byte getByteFromDegree(float degree) {
		return (byte) (int)(degree * 256.0F / 360.0F);
	}
}
