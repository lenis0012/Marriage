package com.lenis0012.bukkit.marriage2;

import com.lenis0012.bukkit.marriage2.commands.Command;
import com.lenis0012.bukkit.marriage2.internal.MarriageCore;
import com.lenis0012.bukkit.marriage2.internal.MarriagePlugin;
import com.lenis0012.bukkit.marriage2.misc.ListQuery;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

/**
 * <h1>Marriage API</h1>
 * <p>
 *   This is the primary interface of the Marriage plugin.
 *   It exposes a static interface that mirrors the internal Marriage plugin, allowing you to access
 *   and manage relationships between the players on this server.
 * </p>
 * <br/>
 * Example:
 * <pre>{@code
 * // Marry 2 players with each other
 * MPlayer mp1 = MarriageAPI.getPlayer(player1);
 * MPlayer mp2 = MarriageAPI.getPlayer(player2);
 * Relationship relationship = MarriageAPI.marry(mp1, mp2);
 *
 * // Divorce 2 players (requires initiating player to be online)
 * mp1.divorce();
 *
 * // Dissolve a relationship (can be done offline)
 * relationship.dissolve();
 *
 * // Get information from a relationship
 * Relationship relationship = MarriageAPI.getPlayer(player1).getActiveRelationship();
 * Location home = relationship.getHome();
 * if (home != null) { // Check if home is set
 *     player1.teleport(home);
 * }
 * }</pre>
 *
 * <p>
 * <h2>Changelog:</h2>
 * 1.03:
 * <ul>
 *     <li>Relocated high-level API from Marriage to MarriageAPI</li>
 *     <li>Deprecated some internal constructs being exposed</li>
 * </ul>
 * 1.02:
 * <ul>
 *     <li>Added {@link com.lenis0012.bukkit.marriage2.events.PlayerMarryEvent PlayerMarryEvent}</li>
 *     <li>Added {@link com.lenis0012.bukkit.marriage2.events.PlayerDivorceEvent PlayerDivorceEvent}</li>
 *     <li>Added {@link Marriage#marry(MPlayer, MPlayer, MPlayer) marry(p1, p2, priest)}</li>
 * </ul>
 * <p>
 * 1.01:
 * <ul>
 *     <li>Added chat spy mode</li>
 * </ul>
 * <p>
 * 1.00:
 * <ul>
 *     <li>Release</li>
 * </ul>
 */
public class MarriageAPI {
    private static final int API_VERSION = 103;

    /**
     * Get the API main instance.
     *
     * @return API instance
     * @deprecated Use {@link MarriageAPI} directly
     */
    @Deprecated
    public static Marriage getInstance() {
        return MarriagePlugin.getCore();
    }

    /**
     * Get current API version.
     *
     * @return API Version
     */
    public static int getAPIVersion() {
        return API_VERSION;
    }

    /**
     * Get current plugin version
     *
     * @return Plugin version
     */
    public static String getPluginVersion() {
        return getPlugin().getDescription().getVersion();
    }

    /**
     * Get name/identifier of API version.
     * Includes API version and plugin version.
     * Used for debugging.
     *
     * @return API Version name
     */
    public static String getName() {
        return "Marriage API v" + API_VERSION + " (plugin v" + getPluginVersion() + ")";
    }

    /**
     * Return a {@link com.lenis0012.bukkit.marriage2.MPlayer MPlayer} instance of a player.
     * If the requested player is not online, their data will be loaded from the database, but it will NOT be cached.
     *
     * @param uuid Unique user id of the wanted player
     * @return {@link com.lenis0012.bukkit.marriage2.MPlayer MPlayer} of the wanted player
     */
    public static MPlayer getMPlayer(UUID uuid) {
        return MarriagePlugin.getCore().getMPlayer(uuid);
    }

    /**
     * Return a {@link com.lenis0012.bukkit.marriage2.MPlayer MPlayer} instance of an online player.
     *
     * @param player Player who'se data is being requested, must be online
     * @return {@link com.lenis0012.bukkit.marriage2.MPlayer MPlayer}, or null if not online
     */
    public static MPlayer getMPlayer(Player player) {
        return MarriagePlugin.getCore().getMPlayer(player);
    }

    /**
     * Get a list of all married players.
     * Note: This is IO, so please put it on an async task.
     *
     * @param pageSize Amount of results per page.
     * @param pageIndex The page you want to fetch
     * @return Fetched page of marriages list
     */
    public static ListQuery getMarriageList(int pageSize, int pageIndex) {
        return MarriagePlugin.getCore().getMarriageList(pageSize, pageIndex);
    }

    /**
     * Marry 2 players with each other.
     *
     * @param player1 Player 1
     * @param player2 Player 2
     * @return The marriage data, null if cancelled via {@link com.lenis0012.bukkit.marriage2.events.PlayerMarryEvent PlayerMarryEvent}
     */
    public static Relationship marry(MPlayer player1, MPlayer player2) {
        return MarriagePlugin.getCore().marry(player1, player2);
    }

    /**
     * Marry 2 players with each other.
     *
     * @param player1 Player 1
     * @param player2 Player 2
     * @param priest  Priest that married the players
     * @return Marriage data, null if cancelled via {@link com.lenis0012.bukkit.marriage2.events.PlayerMarryEvent PlayerMarryEvent}
     */
    public static Relationship marry(MPlayer player1, MPlayer player2, MPlayer priest) {
        return MarriagePlugin.getCore().marry(player1, player2, priest);
    }

    /**
     * Register a subcommand to the /marry command.
     *
     * @param commandClasses Classes of the sub commands to be registered.
     */
    public static void registerCommands(Class<? extends Command>... commandClasses) {
        ((MarriageCore)MarriagePlugin.getCore()).registerCommands(commandClasses);
    }

    /**
     * Get the plugin instance.
     *
     * @return Plugin instance.
     */
    public static Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin("Marriage");
    }
}
