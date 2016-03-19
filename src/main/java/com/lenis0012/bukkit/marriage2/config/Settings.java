package com.lenis0012.bukkit.marriage2.config;

import com.lenis0012.pluginutils.modules.configuration.mapping.ConfigHeader;
import com.lenis0012.pluginutils.modules.configuration.mapping.ConfigOption;

public class Settings {
	/**
	 * Uncatagorized ConfigOption
	 */
	public static final ConfigOption<Integer> REQUEST_EXPRY = new ConfigOption<>("requestExpiry", 60);
    public static final ConfigOption<Boolean> ENABLE_PRIEST = new ConfigOption<>("enable-priests", false);

    /**
     * Cooldown
     */
    public static final ConfigOption<Integer> COOLDOWN_KISS = new ConfigOption<>("cooldown.kiss", 2);

    /**
     * Chat
     */
	@ConfigHeader(path = "chat", value = {
			"Chat, set the format of private messages and in-chat status.",
			"Supported tags for chat: {partner}. for pm: {name}, {message}",
            "Icons are always available: {icon:heart}, {icon:male}, {icon:female}, {icon:genderless}",
			"If you use a custom chat plugin, put {marriage_status} in the format and set force-status-format to false",
            "To show genders in chat, put {marriage_gender} in chat plugin format"
	})
    public static final ConfigOption<String> PM_FORMAT = new ConfigOption<>("chat.pm-format", "&4{icon:heart}&c{name}&4{icon:heart} &7{message}");
    public static final ConfigOption<String> CHAT_FORMAT = new ConfigOption<>("chat.status-format", "&4&l<3 &r");
    public static final ConfigOption<Boolean> FORCE_FORMAT = new ConfigOption<>("chat.force-status-format", true);
    public static final ConfigOption<String> PREFIX_MALE = new ConfigOption<>("chat.male-prefix", "&b{icon:male} &r");
    public static final ConfigOption<String> PREFIX_FEMALE = new ConfigOption<>("chat.female-prefix", "&d{icon:female} &r");
    public static final ConfigOption<String> PREFIX_GENDERLESS = new ConfigOption<>("chat.genderless-prefix", "");

    /**
     * Kissing
     */
	@ConfigHeader(path = "kisses", value = {
			"Kissing, display hearts when 2 married players kiss eachother.",
			"The amount of hearts is a random number between min and max."
	})
    public static final ConfigOption<Boolean> KISSES_ENABLED = new ConfigOption<>("kisses.enabled", true);
    public static final ConfigOption<Integer> KISSES_AMOUNT_MIN = new ConfigOption<>("kisses.amount-min", 5);
    public static final ConfigOption<Integer> KISSES_AMOUNT_MAX = new ConfigOption<>("kisses.amount-max", 10);

    /**
     * Economy
     */
	@ConfigHeader({"Economy settings, uses Vault.", "enable 'show-on-help' to show prices in help command."})
	public static final ConfigOption<Boolean> ECONOMY_ENABLED = new ConfigOption<>("economy.enabled", false);
	public static final ConfigOption<Boolean> ECONOMY_SHOW_PRICE = new ConfigOption<>("economy.show-on-help", true);
	public static final ConfigOption<Double> PRICE_MARRY = new ConfigOption<>("economy.marriage-price", 100.0);
	public static final ConfigOption<Double> PRICE_TELEPORT = new ConfigOption<>("economy.teleport-price", 0.0);
	public static final ConfigOption<Double> PRICE_SETHOME = new ConfigOption<>("economy.sethome-price", 0.0);
	public static final ConfigOption<Double> PRICE_DIVORCE = new ConfigOption<>("economy.divorce-price", 0.0);

	/**
	 * Sharing
	 */
//	@ConfigHeader(path = "share", value = {
//			"Change these values to configure sharing.",
//			"Sharing must be enabled by the partners using /marry share inventory/money",
//			"",
//			"Economy sharing | Supported plugins: None.",
//			"Inventory sharing | Supported versions: 1.9"
//	})
//	public static final ConfigOption<Boolean> SHARE_INV_ENABLED = new ConfigOption<>("share.inventory.enabled", true);
//	public static final ConfigOption<Boolean> SHARE_ECON_ENABLED = new ConfigOption<>("share.economy.enabled", true);
//	@ConfigHeader("Supported: TAKE_ALL, SPLIT_EVENLY, SPLIT_FAIRLY, SPLIT_ORIGINALLY")
//	public static final ConfigOption<String> SHARE_ECON_SPLIT = new ConfigOption<>("share.economy.split", "SPLIT_EVENLY");

	/**
	 * Updater
	 */
	@ConfigHeader(path = "updater", value = {
			"Updater settings, checks for updates. We recommend to keep this enabled.",
			"Available channels: RELEASE, BETA, ALPHA"
	})
	public static final ConfigOption<Boolean> ENABLE_UPDATE_CHACKER = new ConfigOption<>("updater.enabled", true);
    public static final ConfigOption<Boolean> ENABLE_CHANGELOG = new ConfigOption<>("updater.changelog", true);
	public static final ConfigOption<String> UPDATER_CHANNEL = new ConfigOption<>("updater.channel", "BETA");
}