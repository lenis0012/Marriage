package com.lenis0012.bukkit.marriage2.commands;

import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.Relationship;
import com.lenis0012.bukkit.marriage2.config.Message;
import com.lenis0012.bukkit.marriage2.internal.MarriageCore;
import org.bukkit.Location;

public class CommandHome extends Command {

    public CommandHome(MarriageCore marriage) {
        super(marriage, "home");
        setDescription(Message.COMMAND_HOME.toString());
    }

    @Override
    public void execute() {
        MPlayer mPlayer = marriage.getMPlayer(player);
        Relationship marriage = mPlayer.getMarriage();
        if(marriage == null) {
            reply(Message.NOT_MARRIED);
            return;
        }

        Location home = marriage.getHome();
        if(home == null) {
            reply(Message.HOME_NOT_SET);
            return;
        }

        player.teleport(home);
        reply(Message.HOME_TELEPORT);
    }
}