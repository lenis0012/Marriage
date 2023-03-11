package com.lenis0012.bukkit.marriage2.command;

import com.lenis0012.bukkit.marriage2.config.Message;
import com.lenis0012.bukkit.marriage2.internal.MarriageCore;
import com.lenis0012.bukkit.marriage2.internal.MarriagePlugin;
import com.lenis0012.bukkit.marriage2.misc.ListQuery;
import com.lenis0012.pluginutils.command.api.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class ListCommand {
    private final MarriagePlugin plugin;
    private final MarriageCore core;

    public ListCommand(MarriagePlugin plugin, MarriageCore core) {
        this.plugin = plugin;
        this.core = core;
    }

    @Command("/marry list")
    @Command(value = "/marry list <page>", hidden = true)
    @Description("List all relationships on the server")
    @Permission("marry.list")
    public void listRelationships(CommandSender sender, Integer page) {
        int pageIndex = page != null ? page - 1 : 0;
        if(pageIndex < 0) {
            throw new CommandException(Message.NEGATIVE_NUMBER);
        }

        Message.FETCHING_LIST.send(sender);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            ListQuery query = core.getMarriageList(10, pageIndex);
            query.send(sender);
        });
    }
}
