package com.lenis0012.bukkit.marriage2.internal;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

public class MarriagePlugin extends JavaPlugin {
	@SuppressWarnings("unchecked")
	private final List<Method>[] methods = new List[Register.Type.values().length];
	private final MarriageCore core;
	
	public MarriagePlugin() {
		this.core = new MarriageCore(this);
		
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
		for(Method method : methods[type.ordinal()]) {
			Register register = method.getAnnotation(Register.class);
			getLogger().log(Level.INFO, "Loading " + register.name() + "...");
			try {
				method.invoke(core);
			} catch (Exception e) {
				getLogger().log(Level.SEVERE, "Failed to load " + register.name(), e);
			}
		}
		
		getLogger().log(Level.INFO, type.getCompletionMessage());
	}
}