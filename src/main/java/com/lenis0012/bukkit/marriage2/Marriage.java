package com.lenis0012.bukkit.marriage2;

import java.util.UUID;
import java.util.logging.Logger;

import com.lenis0012.bukkit.marriage2.internal.Dependencies;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.lenis0012.bukkit.marriage2.commands.Command;
import com.lenis0012.bukkit.marriage2.misc.BConfig;
import com.lenis0012.bukkit.marriage2.misc.ListQuery;

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
	 * Get a list of all married players.
	 * Note: This is IO, so please put it on an async task.
	 * 
	 * @param scale Amount of results per page.
	 * @param page The page you want to fetch
	 * @return Fetched page of marriages list
	 */
	ListQuery getMarriageList(int scale, int page);

	/**
	 * Marry 2 players with eachother.
	 *
	 * @param player1 Player 1
	 * @param player2 Player 2
	 * @return The marriage data
	 */
	MData marry(MPlayer player1, MPlayer player2);
	
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

	/**
	 * Plugin dependencies.
	 * - Vault economy
	 *
	 * @return Dependencies
     */
	Dependencies dependencies();
}