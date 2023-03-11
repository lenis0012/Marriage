package com.lenis0012.bukkit.marriage2.command.context;

import com.lenis0012.pluginutils.command.api.*;
import com.lenis0012.pluginutils.command.defaults.DefaultMessages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class CommandDefaults {

    @Context
    @Resolver(PlayerArg.class)
    public PlayerArg resolveCommandingPlayer(CommandContext context) {
        return new PlayerArg(context.getPlayerSender());
    }

    @Resolver(PlayerArg.class)
    public PlayerArg resolveCommandingPlayer(String input) {
        Player player = Bukkit.getPlayer(input);
        if(player == null) {
            throw new CommandException(DefaultMessages.INVALID_PLAYER, input);
        }
        return new PlayerArg(player);
    }

    @Completion(PlayerArg.class)
    public List<String> completeCommandingPlayer() {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
    }
}
