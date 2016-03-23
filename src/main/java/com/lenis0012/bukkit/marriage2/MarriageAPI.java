package com.lenis0012.bukkit.marriage2;

import com.lenis0012.bukkit.marriage2.internal.MarriagePlugin;

public class MarriageAPI {
    private static final int API_VERSION = 100;

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
}
