package me.lenis0012.mr.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.bergerkiller.bukkit.common.reflection.classes.EntityTypesRef;

import net.minecraft.server.v1_5_R1.EntityTypes;


public class ReflectionUtil {
	private static List<Class<?>> addedClasses = new ArrayList<Class<?>>();

	@SuppressWarnings("rawtypes")
	public static void registerEntityType(Class<?> cbClass, String cbType, int cbID) {
		if(!addedClasses.contains(cbClass)) {
			try {
				Plugin bkc = Bukkit.getServer().getPluginManager().getPlugin("BKCommonLib");
				if(bkc != null)
					EntityTypesRef.register(cbClass, cbType, cbID);
				else {
					//data format start
					Class[] tmp = new Class[3];
					tmp[0] = Class.class;
					tmp[1] = String.class;
					tmp[2] = int.class;
					//data format end

					Method entities = EntityTypes.class.getDeclaredMethod("a", tmp);
					entities.setAccessible(true);

					//write custom data to the entity list
					entities.invoke(entities, cbClass, cbType, cbID);
				}

				addedClasses.add(cbClass);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
