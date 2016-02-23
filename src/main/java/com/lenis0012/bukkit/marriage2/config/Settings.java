package com.lenis0012.bukkit.marriage2.config;

import java.util.List;

import com.google.common.collect.Lists;

import org.bukkit.configuration.file.FileConfiguration;

import com.lenis0012.bukkit.marriage2.internal.MarriageCore;

public class Settings<T> {
	private static final List<Settings<?>> cache = Lists.newArrayList();
	
	/**
	 * Values
	 */
	public static final Settings<Integer> REQUEST_EXPRY = new Settings<>("requestExpiry", 60);
//	public static final Settings<Integer> COOLDOWN_MARRY = new Settings<>("cooldown.marry", 120);
//	public static final Settings<Integer> COOLDOWN_GIFT = new Settings<>("cooldown.gift", 0);
//	public static final Settings<Integer> COOLDOWN_DIVORCE = new Settings<>("cooldown.divorce", 0);
	public static final Settings<Integer> COOLDOWN_KISS = new Settings<>("cooldown.kiss", 2);

    public static final Settings<Boolean> KISSES_ENABLED = new Settings<>("kisses.enabled", true);
    public static final Settings<Integer> KISSES_AMOUNT_MIN = new Settings<>("kisses.amount-min", 5);
    public static final Settings<Integer> KISSES_AMOUNT_MAX = new Settings<>("kisses.amount-max", 10);

	public static final Settings<Boolean> ENABLE_PRIEST = new Settings<>("enable-priests", false);
	public static final Settings<Boolean> ENABLE_UPDATE_CHACKER = new Settings<>("updater.enabled", true);
    public static final Settings<Boolean> ENABLE_CHANGELOG = new Settings<>("updater.changelog", true);
	
	private final String key;
	private final T def;
	private T value;
	
	private Settings(String key, T def) {
		cache.add(this);
		this.key = key;
		this.def = def;
	}
	
	public T value() {
		return value;
	}
	
	@SuppressWarnings("unchecked")
	private void reload(FileConfiguration config) {
		if(config.contains(key)) {
			this.value = (T) config.get(key);
		} else {
			config.set(key, def);
			this.value = def;
		}
	}
	
	public static final void reloadAll(MarriageCore core) {
		FileConfiguration config = core.getPlugin().getConfig();
		for(Settings<?> setting : cache) {
			setting.reload(config);
		}
		
		core.getPlugin().saveConfig();
	}
	
	public static final List<Settings<?>> values() {
		return cache;
	}
}