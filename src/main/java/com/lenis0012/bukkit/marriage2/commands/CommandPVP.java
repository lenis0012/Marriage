package com.lenis0012.bukkit.marriage2.commands;

import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.Relationship;
import com.lenis0012.bukkit.marriage2.config.Message;
import com.lenis0012.bukkit.marriage2.config.Permissions;
import com.lenis0012.bukkit.marriage2.internal.MarriageCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandPVP extends Command {
    public CommandPVP(MarriageCore marriage) {
        super(marriage, "pvp");
        setPermission(Permissions.PVP_TOGGLE);
        setDescription(Message.COMMAND_PVP.toString());
        setUsage(Message.ON_OFF.toString());
    }

    @Override
    public void execute() {
        MPlayer mPlayer = marriage.getMPlayer(player);
        Relationship marriage = mPlayer.getMarriage();
        if(marriage == null) {
            reply(Message.NOT_MARRIED);
            return;
        }

        boolean value = value(marriage);
        marriage.setPVPEnabled(value);
        reply(value ? Message.PVP_ENABLED : Message.PVP_DISABLED);

        Player partner = Bukkit.getPlayer(marriage.getOtherPlayer(player.getUniqueId()));
        if(partner == null) {
            return;
        }

        reply(partner, Message.PARTNER_PVP);
    }

    private boolean value(Relationship marriage) {
        if(getArgLength() == 0) {
            // Toggle
            return !marriage.isPVPEnabled();
        }

        return getArg(0).equalsIgnoreCase("on");
    }
}
