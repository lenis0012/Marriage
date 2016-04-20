package com.lenis0012.bukkit.marriage2;

import com.lenis0012.bukkit.marriage2.internal.MarriagePlugin;

/**
 * Marriage API.
 *
 * <b>Changelog:</b>
 * 1.01:
 * <ul>
 *     <li>Added chat spy mode</li>
 * </ul>
 *
 * 1.00:
 * <ul>
 *     <li>Release</li>
 * </ul>
 */
public class MarriageAPI {
    private static final int API_VERSION = 101;

    /**
     * Get the API main instance.
     *
     * @return
     */
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
        return MarriagePlugin.getInstance().getDescription().getVersion();
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
}
