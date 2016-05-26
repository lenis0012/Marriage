package com.lenis0012.bukkit.marriage2.config;

import com.lenis0012.bukkit.marriage2.internal.MarriagePlugin;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.logging.Level;

public enum Permissions {
    /**
     * Kits
     */
    ALL("marry.*", -1),
    ADMIN("marry.admin", 0),
    DEFAULT("marry.default", 0),
    /**
     * Admin commands
     */
    UPDATE("marry.update", 1),
    CHAT_SPY("marry.chatspy", 1),
    /**
     * Player commands
     */
    MARRY("marry.marry"),
    LIST("marry.list"),
    TELEPORT("marry.tp"),
    HOME("marry.home"),
    SET_HOME("marry.sethome"),
    GIFT("marry.gift"),
    CHAT("marry.chat"),
    SEEN("marry.seen");

    private static boolean vaultEnabled = false;
    private static Permission permissionService;

    public boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServicesManager().getRegistration(Permission.class);
        if(permissionProvider != null) {
            permissionService = permissionProvider.getProvider();
            vaultEnabled = true;
            MarriagePlugin.getCore().getLogger().log(Level.INFO, "Hooked with " + permissionService.getName() + " using Vault!");
        }
        return permissionService != null;
    }

    private final String node;
    private final int parent;

    Permissions(String node) {
        this(node, 2);
    }

    Permissions(String node, int parent) {
        this.node = node;
        this.parent = parent;
    }

    /**
     * Check whether a command sender has a permission.
     *
     * @param sender to check for
     * @return True if has permission, False otherwise
     */
    public boolean has(CommandSender sender) {
        if(parent >= 0 && values()[parent].has(sender)) {
            return true;
        }

        return vaultEnabled ? permissionService.has(sender, node) : sender.hasPermission(node);
    }

    /**
     * Get a permission by it's node.
     *
     * @param node of the permission
     * @return Permission
     */
    public static Permissions getByNode(String node) {
        for(Permissions permission : values()) {
            if(permission.node.equalsIgnoreCase(node)) {
                return permission;
            }
        }

        return null;
    }
}
