package com.lenis0012.bukkit.marriage2.internal;

import com.lenis0012.bukkit.marriage2.Marriage;
import com.lenis0012.bukkit.marriage2.commands.Command;
import com.lenis0012.bukkit.marriage2.misc.BConfig;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.logging.Logger;

public abstract class MarriageBase implements Marriage {
    protected final MarriagePlugin plugin;
    private MarriageCommandExecutor commandExecutor;

    public MarriageBase(MarriagePlugin plugin) {
        this.plugin = plugin;
    }

    void enable() {
        this.commandExecutor = new MarriageCommandExecutor(this);
        plugin.getCommand("marry").setExecutor(commandExecutor);
    }

    @Override
    public void register(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    @Override
    @Deprecated
    public void register(Class<? extends Command> commandClass, Class<? extends Command>... commandClasses) {
        registerCommands(commandClass);
        registerCommands(commandClasses);
    }

    public void registerCommands(Class<? extends Command>... commandClasses) {
        for(Class<? extends Command> cmdclass : commandClasses) {
            commandExecutor.register(cmdclass);
        }
    }

    @Override
    public BConfig getBukkitConfig(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        return new BConfig(this, file);
    }

    @Override
    public Logger getLogger() {
        return plugin.getLogger();
    }

    @Override
    public MarriagePlugin getPlugin() {
        return plugin;
    }

    public MarriageCommandExecutor getCommandExecutor() {
        return commandExecutor;
    }
}