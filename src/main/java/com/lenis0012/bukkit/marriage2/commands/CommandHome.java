package com.lenis0012.bukkit.marriage2.commands;

import com.lenis0012.bukkit.marriage2.MData;
import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.Marriage;
import com.lenis0012.bukkit.marriage2.config.Message;
import org.bukkit.Location;

public class CommandHome extends Command {

    public CommandHome(Marriage marriage) {
        super(marriage, "home");
        setDescription(Message.COMMAND_HOME.toString());
    }

    @Override
    public void execute() {
        MPlayer mPlayer = marriage.getMPlayer(player);
        MData marriage = mPlayer.getMarriage();
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