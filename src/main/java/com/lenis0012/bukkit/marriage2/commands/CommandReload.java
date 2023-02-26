package com.lenis0012.bukkit.marriage2.commands;

import com.lenis0012.bukkit.marriage2.config.Message;
import com.lenis0012.bukkit.marriage2.config.Permissions;
import com.lenis0012.bukkit.marriage2.internal.MarriageCore;
import com.lenis0012.bukkit.marriage2.internal.MarriagePlugin;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandReload extends Command {

    public CommandReload(MarriageCore marriage) {
        super(marriage, "reload");

        // Command options
        setDescription("Reload configuration settings");
        setPermission(Permissions.RELOAD);
        setAllowConsole(true);
        setHidden(true);
    }

    @Override
    public void execute() {
        JavaPlugin.getPlugin(MarriagePlugin.class).reloadSettings();
        reply(Message.CONFIG_RELOAD);
    }
}
