package com.lenis0012.bukkit.marriage2.command;

import com.lenis0012.bukkit.marriage2.Relationship;
import com.lenis0012.bukkit.marriage2.command.context.PlayerArg;
import com.lenis0012.bukkit.marriage2.config.Message;
import com.lenis0012.pluginutils.command.api.*;

public class HomeCommand {

    @Command("/marry sethome")
    @Description("Change your shared home location")
    @Permission("marriage.sethome")
    public void setHome(@Context PlayerArg sender) {
        if(!sender.data().isMarried()) {
            throw new CommandException(Message.NOT_MARRIED);
        }
        sender.data().getActiveRelationship().setHome(sender.player().getLocation().clone());
        Message.HOME_SET.send(sender.player());
    }

    @Command("/marry home")
    @Description("Visit your shared home")
    @Permission("marry.home")
    public void visitHome(@Context PlayerArg sender) {
        if(!sender.data().isMarried()) {
            throw new CommandException(Message.NOT_MARRIED);
        }

        final Relationship relationship = sender.data().getActiveRelationship();
        if(!relationship.isHomeSet()) {
            throw new CommandException(Message.HOME_NOT_SET);
        }

        sender.player().teleport(relationship.getHome());
        Message.HOME_TELEPORT.send(sender.player());
    }
}
