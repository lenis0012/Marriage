package com.lenis0012.bukkit.marriage2.misc.reflection;

import com.google.common.collect.Maps;
import com.lenis0012.bukkit.marriage2.misc.reflection.Reflection.ClassReflection;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public class Packets {
    private static final Map<String, ClassReflection> packetClasses = Maps.newConcurrentMap();
    private static final Method GET_HANDLE = Reflection.getCBMethod("entity.CraftPlayer", "getHandle");
    private static final Field PLAYER_CONNECTION = Reflection.getNMSField("EntityPlayer", "playerConnection");
    private static final Method SEND_PACKET = Reflection.getNMSMethod("PlayerConnection", "sendPacket", Reflection.getNMSClass("Packet"));

    public static Object createPacket(String name) {
        ClassReflection ref = packetClasses.get(name);
        if(ref == null) {
            ref = new ClassReflection(Reflection.getNMSClass(name));
            packetClasses.put(name, ref);
        }

        return ref.newInstance();
    }

    public static void set(Object packet, String fieldName, Object value) {
        ClassReflection ref = packetClasses.get(packet.getClass().getSimpleName());
        ref.setFieldValue(fieldName, packet, value);
    }

    public static void send(Player player, Object packet) {
        Object entityPlayer = Reflection.invokeMethod(GET_HANDLE, player);
        Object playerConnection = Reflection.getFieldValue(PLAYER_CONNECTION, entityPlayer);
        Reflection.invokeMethod(SEND_PACKET, playerConnection, packet);
    }
}
