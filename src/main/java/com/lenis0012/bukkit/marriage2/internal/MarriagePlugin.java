package com.lenis0012.bukkit.marriage2.internal;

import java.io.File;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lenis0012.pluginutils.PluginHolder;
import com.lenis0012.pluginutils.modules.configuration.ConfigurationModule;
import com.lenis0012.pluginutils.modules.packets.PacketModule;

import com.lenis0012.bukkit.marriage2.Marriage;
import org.yaml.snakeyaml.Yaml;

public class MarriagePlugin extends PluginHolder {
	private static MarriageCore core;
	
	public static Marriage getCore() {
		return core;
	}
	
	@SuppressWarnings("unchecked")
	private final List<Method>[] methods = new List[Register.Type.values().length];
	
	public MarriagePlugin() {
		super(PacketModule.class,
				ConfigurationModule.class);
		core = new MarriageCore(this);
		
		//Scan methods
		for(int i = 0; i < methods.length; i++) {
			methods[i] = Lists.newArrayList();
		}
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

	protected File getPluginFile() {
		return getFile();
	}
	
	@Override
	public void onLoad() {
		executeMethods(Register.Type.LOAD);
	}
	
	@Override
	public void enable() {
		executeMethods(Register.Type.ENABLE);
	}
	
	@Override
	public void disable() {
		executeMethods(Register.Type.DISABLE);
	}
	
	private void executeMethods(Register.Type type) {
		List<Method> list = Lists.newArrayList(methods[type.ordinal()]);
		while(!list.isEmpty()) {
			Method method = null;
			int lowestPriority = Integer.MAX_VALUE;
			for(Method m : list) {
				Register register = m.getAnnotation(Register.class);
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