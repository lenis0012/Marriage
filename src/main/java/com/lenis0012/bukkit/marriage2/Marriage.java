package com.lenis0012.bukkit.marriage2;

import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.event.Listener;

import com.lenis0012.bukkit.marriage2.commands.Command;
import com.lenis0012.bukkit.marriage2.internal.MarriagePlugin;
import com.lenis0012.bukkit.marriage2.misc.BConfig;

public interface Marriage {
	
	/**
	 * Return a {@link com.lenis0012.bukkit.marriage2.misc.BConfig} from a YAML file.
	 * 
	 * @param file File name.
	 * @return Bukkit configuration instance.
	 */
	public BConfig getBukkitConfig(String file);
	
	/**
	 * Return a {@link com.lenis0012.bukkit.marriage2.MPlayer} instance of a player.
	 * 
	 * @param uuid Unique user id of the wanted player.
	 * @return {@link com.lenis0012.bukkit.marriage2.MPlayer} of the wanted player.
	 */
	public MPlayer getMPlayer(UUID uuid);
	
	/**
	 * Register a {@link org.bukkit.event.Listener} to this plugin.
	 * 
	 * @param listener Listener to be registered.
	 */
	public void register(Listener listener);
	
	/**
	 * Register a subcommand to the /marry command.
	 * 
	 * @param commandClass Class of the sub command to be registered.
	 */
	public void register(Class<? extends Command> commandClass);
	
	/**
	 * Get the plugin logger instance.
	 * 
	 * @return Plugin logger.
	 */
	public Logger getLogger();
	
	/**
	 * Get the plugin instance.
	 * 
	 * @return Plugin instance.
	 */
	public MarriagePlugin getPlugin();
}