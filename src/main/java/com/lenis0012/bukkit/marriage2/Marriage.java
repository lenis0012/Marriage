package com.lenis0012.bukkit.marriage2;

import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.lenis0012.bukkit.marriage2.commands.Command;
import com.lenis0012.bukkit.marriage2.misc.BConfig;

public interface Marriage {
	/**
	 * Return a {@link com.lenis0012.bukkit.marriage2.misc.BConfig} from a YAML file.
	 * 
	 * @param file File name.
	 * @return Bukkit configuration instance.
	 */
	BConfig getBukkitConfig(String file);
	
	/**
	 * Return a {@link com.lenis0012.bukkit.marriage2.MPlayer} instance of a player.
	 * 
	 * @param uuid Unique user id of the wanted player.
	 * @return {@link com.lenis0012.bukkit.marriage2.MPlayer} of the wanted player.
	 */
	MPlayer getMPlayer(UUID uuid);
	
	/**
	 * Marry 2 players to eachother.
	 * 
	 * @param player1 Player 1
	 * @param player2 Player 2
	 */
	void marry(MPlayer player1, MPlayer player2);
	
	/**
	 * Register a {@link org.bukkit.event.Listener} to this plugin.
	 * 
	 * @param listener Listener to be registered.
	 */
	void register(Listener listener);
	
	/**
	 * Register a subcommand to the /marry command.
	 * 
	 * @param commandClass Class of the sub command to be registered.
	 */
	void register(Class<? extends Command> commandClass);
	
	/**
	 * Get the plugin logger instance.
	 * 
	 * @return Plugin logger.
	 */
	Logger getLogger();
	
	/**
	 * Get the plugin instance.
	 * 
	 * @return Plugin instance.
	 */
	Plugin getPlugin();
}