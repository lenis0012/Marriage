package com.lenis0012.bukkit.marriage2.internal;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

import com.lenis0012.bukkit.marriage2.Marriage;

public class MarriagePlugin extends JavaPlugin {
	private static MarriageCore core;
	
	public static Marriage getInstance() {
		return core;
	}
	
	@SuppressWarnings("unchecked")
	private final List<Method>[] methods = new List[Register.Type.values().length];
	
	public MarriagePlugin() {
		core = new MarriageCore(this);
		
		//Scan methods
		Arrays.fill(methods, new ArrayList<Method>());
		scanMethods(core.getClass());
	}
	
	private void scanMethods(Class<?> clazz) {
		if(clazz == null) {
			return;
		}
		
		// Loop through all methods in class
		for(Method method : clazz.getMethods()) {
			Register register = method.getAnnotation(Register.class);
			if(register != null) {
				methods[register.type().ordinal()].add(method);
			}
		}
		
		// Scan methods in super class
		scanMethods(clazz.getSuperclass());
	}
	
	@Override
	public void onLoad() {
		executeMethods(Register.Type.LOAD);
	}
	
	@Override
	public void onEnable() {
		executeMethods(Register.Type.ENABLE);
	}
	
	@Override
	public void onDisable() {
		executeMethods(Register.Type.DISABLE);
	}
	
	private void executeMethods(Register.Type type) {
		List<Method> list = new ArrayList<Method>(methods[type.ordinal()]);
		while(!list.isEmpty()) {
			Method method = null;
			int lowestPriority = Integer.MAX_VALUE;
			for(Method m : list) {
				Register register = method.getAnnotation(Register.class);
				if(register.priority() < lowestPriority) {
					method = m;
					lowestPriority = register.priority();
				}
			}
			
			if(method != null) {
				list.remove(method);
				Register register = method.getAnnotation(Register.class);
				getLogger().log(Level.INFO, "Loading " + register.name() + "...");
				try {
					method.invoke(core);
				} catch (Exception e) {
					getLogger().log(Level.SEVERE, "Failed to load " + register.name(), e);
				}
			} else {
				list.clear();
			}
		}
		
		getLogger().log(Level.INFO, type.getCompletionMessage());
	}
}