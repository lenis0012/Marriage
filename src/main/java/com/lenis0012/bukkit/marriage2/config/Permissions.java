package com.lenis0012.bukkit.marriage2.config;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public enum Permissions {
    /**
     * Kitds
     */
    ALL("marry.*", PermissionDefault.FALSE, -1),
    ADMIN("marry.admin", PermissionDefault.OP, 0),
    DEFAULT("marry.default", PermissionDefault.OP, 0),
    /**
     * Admin commands
     */
    UPDATE("marry.update", PermissionDefault.FALSE, 1),
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

//    public static Permission permissionService;
//
//    public boolean setupPermissions() {
//        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServicesManager().getRegistration(Permission.class);
//        if(permissionProvider != null) {
//            permissionService = permissionProvider.getProvider();
//        }
//        return permissionService != null;
//    }

    private final String node;
    private final PermissionDefault defaultSetting;
    private final int parent;
    private Permission permission;

    Permissions(String node) {
        this(node, PermissionDefault.FALSE);
    }

    Permissions(String node, PermissionDefault defaultSetting) {
        this(node, defaultSetting, 2);
    }

    Permissions(String node, PermissionDefault defaultSetting, int parent) {
        this.node = node;
        this.defaultSetting = defaultSetting;
        this.parent = parent;
        this.permission = new Permission(node, null, defaultSetting);
        Bukkit.getPluginManager().addPermission(permission);
    }

    /**
     * Check whether a command sender has a permission.
     *
     * @param sender to check for
     * @return True if has permission, False otherwise
     */
    public boolean has(CommandSender sender) {
        return sender.hasPermission(permission);
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

    /**
     * Set child relations
     */
    public static void setupChildRelations() {
        for(Permissions perm : values()) {
            if(perm.parent < 0) continue;
            perm.permission.addParent(values()[perm.parent].permission, true);
        }
    }

    public static void unloadAll() {
        for(Permissions perm : values()) {
            Bukkit.getPluginManager().removePermission(perm.permission);
        }
    }
}
