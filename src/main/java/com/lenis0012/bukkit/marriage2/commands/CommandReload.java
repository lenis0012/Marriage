package com.lenis0012.bukkit.marriage2.commands;

import com.lenis0012.bukkit.marriage2.Marriage;
import com.lenis0012.bukkit.marriage2.config.Message;
import com.lenis0012.bukkit.marriage2.config.Permissions;
import com.lenis0012.bukkit.marriage2.internal.MarriageCore;
import com.lenis0012.bukkit.marriage2.internal.MarriagePlugin;

public class CommandReload extends Command {

    public CommandReload(Marriage marriage) {
        super(marriage, "reload");

        // Command options
        setDescription("Reload configuration settings");
        setPermission(Permissions.RELOAD);
        setAllowConsole(true);
        setHidden(true);
    }

    @Override
    public void execute() {
        ((MarriageCore) MarriagePlugin.getCore()).reloadSettings();
        reply(Message.CONFIG_RELOAD);
    }
}
