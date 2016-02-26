package com.lenis0012.bukkit.marriage2.config;

import java.util.List;

import com.google.common.collect.Lists;

import org.bukkit.configuration.file.FileConfiguration;

import com.lenis0012.bukkit.marriage2.internal.MarriageCore;

public class Settings<T> {
	private static final List<Settings<?>> cache = Lists.newArrayList();
	
	/**
	 * Uncatagorized settings
	 */
	public static final Settings<Integer> REQUEST_EXPRY = new Settings<>("requestExpiry", 60);
    public static final Settings<Boolean> ENABLE_PRIEST = new Settings<>("enable-priests", false);

    /**
     * Cooldown
     */
    public static final Settings<Integer> COOLDOWN_KISS = new Settings<>("cooldown.kiss", 2);

    /**
     * Chat
     */
    public static final Settings<String> PM_FORMAT = new Settings<>("chat.pm-format", "&4{heart}&c{name}&4{heart} &7{message}");
    public static final Settings<String> CHAT_FORMAT = new Settings<>("chat.status-format", "&4&l<3 &r");
    public static final Settings<Boolean> FORCE_FORMAT = new Settings<>("chat.force-status-format", true);

    /**
     * Kissing
     */
    public static final Settings<Boolean> KISSES_ENABLED = new Settings<>("kisses.enabled", true);
    public static final Settings<Integer> KISSES_AMOUNT_MIN = new Settings<>("kisses.amount-min", 5);
    public static final Settings<Integer> KISSES_AMOUNT_MAX = new Settings<>("kisses.amount-max", 10);

    /**
     * Economy
     */
    public static final Settings<Boolean> ECONOMY_ENABLED = new Settings<>("economy.enabled", false);
    public static final Settings<Boolean> ECONOMY_SHOW = new Settings<>("economy.show-on-help", true);
    public static final Settings<Double> PRICE_MARRY = new Settings<>("economy.marriage-price", 100.0);
    public static final Settings<Double> PRICE_TELEPORT = new Settings<>("economy.teleport-price", 0.0);
    public static final Settings<Double> PRICE_SETHOME = new Settings<>("economy.sethome-price", 0.0);
    public static final Settings<Double> PRICE_DIVORCE = new Settings<>("economy.divorce", 0.0);

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

    private void writeToConfig(FileConfiguration config) {
        config.set(key, value);
    }
	
	@SuppressWarnings("unchecked")
	private void reload(FileConfiguration config) {
		if(config.contains(key)) {
			this.value = (T) config.get(key);
		} else {
			this.value = def;
		}
	}
	
	public static final void reloadAll(MarriageCore core, boolean initial) {
		FileConfiguration config = core.getPlugin().getConfig();
		for(Settings<?> setting : cache) {
			setting.reload(config);
		}

        if(initial) {
            // Reset config
            for(String key : config.getKeys(false)) {
                config.set(key, null);
            }

            // Write current values
            for(Settings<?> setting : cache) {
                setting.writeToConfig(config);
            }

            // Write header
            config.options().header("Marriage Reloaded 2.X\n\n" +
                    "Expiry & Cool down is in seconds.\n\n" +
                    "PM format supports: {heart}, {name}, {message}. Chat format supports {heart}\n" +
                    "Note: If you have a custom chat plugin, set forced to false and put {marriage_status}\n" +
                    "somewhere in your chat plugin's format.\n\n" +
                    "Economy setting show-on-help shows the fee when hovering over command.\n\n" +
                    "The rest should be straight forward. if not, comment on bukkitdev.");
            core.getPlugin().saveConfig();
        }
	}
	
	public static final List<Settings<?>> values() {
		return cache;
	}
}