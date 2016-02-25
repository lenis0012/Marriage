package com.lenis0012.bukkit.marriage2.config;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public enum Permissions {
    /**
     * Kitds
     */
    ALL("marry.*", PermissionDefault.FALSE, -1),
    ADMIN("marry.admin", PermissionDefault.OP, 0),
    DEFAULT("marry.default", PermissionDefault.TRUE, 0),
    /**
     * Admin commands
     */
    UPDATE("marry.update", PermissionDefault.OP, 1),
    /**
     * Player commands
     */
    MARRY("marry.marry", PermissionDefault.TRUE),
    LIST("marry.list", PermissionDefault.TRUE),
    TELEPORT("marry.tp", PermissionDefault.TRUE),
    HOME("marry.home", PermissionDefault.TRUE),
    SET_HOME("marry.sethome", PermissionDefault.TRUE),
    GIFT("marry.gift", PermissionDefault.TRUE),
    CHAT("marry.chat", PermissionDefault.TRUE),
    SEEN("marry.seen", PermissionDefault.TRUE);

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

    Permissions(String node, PermissionDefault defaultSetting) {
        this(node, defaultSetting, 2);
    }

    Permissions(String node, PermissionDefault defaultSetting, int parent) {
        this.node = node;
        this.defaultSetting = defaultSetting;
        this.parent = parent;
    }

    public boolean has(CommandSender sender) {
        if(parent >= 0 && values()[parent].has(sender)) {
            return true;
        }
        
        return sender.hasPermission(node);

//        if(permissionService != null) {
//            return permissionService.has(player, node);
//        }
//
//        return defaultSetting.getValue(player.isOp());
    }

    public static Permissions getByNode(String node) {
        for(Permissions permission : values()) {
            if(permission.node.equalsIgnoreCase(node)) {
                return permission;
            }
        }

        return null;
    }
}
