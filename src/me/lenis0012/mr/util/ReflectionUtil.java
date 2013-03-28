package me.lenis0012.mr.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_5_R2.EntityTypes;


public class ReflectionUtil {
	private static List<Class<?>> addedClasses = new ArrayList<Class<?>>();

	@SuppressWarnings("rawtypes")
	public static void registerEntityType(Class<?> cbClass, String cbType, int cbID) {
		if(!addedClasses.contains(cbClass)) {
			try {
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
				entities.setAccessible(false);

				addedClasses.add(cbClass);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
