package com.lenis0012.bukkit.marriage2.internal;

import com.google.common.collect.Maps;
import com.lenis0012.bukkit.marriage2.commands.Command;
import com.lenis0012.bukkit.marriage2.config.Settings;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;

public class MarriageCommandExecutor implements CommandExecutor {
    private final MarriageCore core;
    private final Map<String, Command> commands = Maps.newHashMap();

    public MarriageCommandExecutor(MarriageCore core) {
        this.core = core;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        String subCommand = args.length > 0 ? args[0] : "help";
        Command command = commands.get(subCommand.toLowerCase());
        if(command == null) {
            command = commands.get("marry");
            subCommand = "marry";
        }

        // Assuming that the command is not null now, if it is, then that is a mistake on my side.
        if(args.length > command.getMinArgs()) {
            if(command.getPermission() == null || command.getPermission().has(sender)) {
                if(command.isAllowConsole() || sender instanceof Player) {
                    command.prepare(sender, args);
                    command.execute();
                } else {
                    sender.sendMessage(ChatColor.RED + "You must be a player to execute this command.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You are not permitted to use this command.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You have not specified enough arguments for this command.");
            sender.sendMessage(ChatColor.RED + "Usage: /marry " + subCommand + " " + command.getUsage());
        }

        return true;
    }

    public void register(Class<? extends Command> commandClass) {
        try {
            Command command = commandClass.getConstructor(MarriageCore.class).newInstance(core);
            if(Settings.DISABLED_COMMANDS.value().contains(command.getAliases()[0])) {
                // Command was disabled in config...
                return;
            }

            for(String alias : command.getAliases()) {
                commands.put(alias.toLowerCase(), command);
            }
        } catch(Exception e) {
            JavaPlugin.getPlugin(MarriagePlugin.class).getLogger().log(Level.SEVERE, "Failed to register sub command", e);
        }
    }

    public Collection<Command> getSubCommands() {
        return commands.values();
    }
}