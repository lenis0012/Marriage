package com.lenis0012.bukkit.marriage2.internal;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.reflect.ClassPath;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import com.google.common.collect.Lists;
import com.lenis0012.bukkit.marriage2.Marriage;
import com.lenis0012.bukkit.marriage2.commands.Command;
import com.lenis0012.bukkit.marriage2.misc.BConfig;

public abstract class MarriageBase implements Marriage {
	protected final MarriagePlugin plugin;
	private final ClassPath classPath;
	private MarriageCommandExecutor commandExecutor;
	
	public MarriageBase(MarriagePlugin plugin) {
		this.plugin = plugin;
		try {
			this.classPath = ClassPath.from(getClass().getClassLoader());
		} catch (IOException e) {
			throw new RuntimeException("Failed to intialize class path!", e);
		}
	}
	
	void enable() {
		this.commandExecutor = new MarriageCommandExecutor(this);
		plugin.getCommand("marry").setExecutor(commandExecutor);
	}
	
	@Override
	public void register(Listener listener) {
		Bukkit.getPluginManager().registerEvents(listener, plugin);
	}
	
	@Override
	public void register(Class<? extends Command> commandClass) {
		commandExecutor.regster(commandClass);
	}
	
	@Override
	public BConfig getBukkitConfig(String fileName) {
		File file = new File(plugin.getDataFolder(), fileName);
		return new BConfig(this, file);
	}
	
	@Override
	public Logger getLogger() {
		return plugin.getLogger();
	}
	
	@Override
	public MarriagePlugin getPlugin() {
		return plugin;
	}
	
	public MarriageCommandExecutor getCommandExecutor() {
		return commandExecutor;
	}
	
	@SuppressWarnings("unchecked")
	protected <T> List<Class<? extends T>> findClasses(String pkg, Class<T> type, Object... params) {
		List<Class<? extends T>> list = Lists.newArrayList();
		for(ClassPath.ClassInfo info : classPath.getTopLevelClassesRecursive(pkg)) {
			try {
				Class<?> clazz = Class.forName(info.getName());
				if(type.isAssignableFrom(clazz) && !type.equals(clazz)) {
					list.add((Class<? extends T>) clazz);
				}
			} catch(Exception e) {
				plugin.getLogger().log(Level.WARNING, "Failed to intiate class", e);
			}
		}
		
		return list;
	}
	
	protected <T> List<T> findObjects(String pkg, Class<T> type, Object... params) {
		List<T> list = Lists.newArrayList();
		for(Class<? extends T> clazz : findClasses(pkg, type)) {
			try {
				list.add(type.cast(clazz.getConstructors()[0].newInstance(params)));
			} catch (Exception e) {
				plugin.getLogger().log(Level.WARNING, "Failed to construct class", e);
			}
		}
		
		return list;
	}
}