package com.lenis0012.bukkit.marriage2.commands;

import com.lenis0012.bukkit.marriage2.Marriage;
import com.lenis0012.bukkit.marriage2.config.Message;
import com.lenis0012.bukkit.marriage2.config.Permissions;
import com.lenis0012.bukkit.marriage2.config.Settings;
import com.lenis0012.bukkit.marriage2.internal.MarriagePlugin;
import com.lenis0012.pluginutils.modules.configuration.ConfigurationModule;

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
        MarriagePlugin plugin = (MarriagePlugin) marriage.getPlugin();
        ConfigurationModule module = plugin.getModule(ConfigurationModule.class);
        module.reloadSettings(Settings.class, false);
        reply(Message.CONFIG_RELOAD);
    }
}
