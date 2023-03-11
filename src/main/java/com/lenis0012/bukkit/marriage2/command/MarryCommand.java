package com.lenis0012.bukkit.marriage2.command;

import com.lenis0012.bukkit.marriage2.command.context.PlayerArg;
import com.lenis0012.bukkit.marriage2.config.Message;
import com.lenis0012.bukkit.marriage2.internal.MarriageCore;
import com.lenis0012.bukkit.marriage2.internal.MarriagePlugin;
import com.lenis0012.pluginutils.command.api.Command;
import com.lenis0012.pluginutils.command.api.CommandException;
import com.lenis0012.pluginutils.command.api.Context;
import com.lenis0012.pluginutils.command.api.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class MarryCommand {
    private final MarriageCore core;

    public MarryCommand(MarriageCore core) {
        this.core = core;
    }

    @Command("/marry <player>")
    @Permission("marriage.marry")
    public void marry(@Context PlayerArg sender, PlayerArg target) {
        if(sender.equals(target)) {
            throw new CommandException(Message.MARRY_SELF);
        }
        if(target.data().isMarried()) {
            throw new CommandException(Message.ALREADY_MARRIED);
        }

        // Check whether this is a confirmation
        if(sender.data().isMarriageRequested(target.data().getUniqueId())) {
            confirmMarriage(sender, target);
            return;
        }

        if(target.data().isMarriageRequested(sender.data().getUniqueId())) {
            throw new CommandException(Message.COOLDOWN);
        }

        target.data().requestMarriage(sender.data().getUniqueId());
        Message.MARRIAGE_REQUESTED.send(target.player(), sender.player().getDisplayName());
        Message.REQUEST_SENT.send(sender.player(), target.player().getDisplayName());
    }

    @Command("/marry <player1> <player2>")
    @Permission("marriage.marry.others")
    public void marryOther(CommandSender sender, PlayerArg player1, PlayerArg player2) {
        if(player1.data().isMarried() || player2.data().isMarried()) {
            throw new CommandException(Message.ALREADY_MARRIED);
        }

        confirmMarriage(player1, player2);
    }

    private void confirmMarriage(PlayerArg requestee, PlayerArg requester) {
        core.marry(requester.data(), requestee.data());
        Bukkit.broadcastMessage(String.format(Message.MARRIED.toString(), requester.player().getDisplayName(), requestee.player().getDisplayName()));
    }
}
