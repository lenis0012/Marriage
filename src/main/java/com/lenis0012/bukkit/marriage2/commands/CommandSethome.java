package com.lenis0012.bukkit.marriage2.commands;

import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.Relationship;
import com.lenis0012.bukkit.marriage2.config.Message;
import com.lenis0012.bukkit.marriage2.config.Settings;
import com.lenis0012.bukkit.marriage2.internal.MarriageCore;

public class CommandSethome extends Command {

    public CommandSethome(MarriageCore marriage) {
        super(marriage, "sethome");
        setExecutionFee(Settings.PRICE_SETHOME);
        setDescription(Message.COMMAND_SETHOME.toString());
    }

    @Override
    public void execute() {
        MPlayer mPlayer = marriage.getMPlayer(player);
        Relationship marriage = mPlayer.getMarriage();
        if(marriage == null) {
            reply(Message.NOT_MARRIED);
            return;
        }

        if(!payFee()) return;
        marriage.setHome(player.getLocation().clone());
        reply(Message.HOME_SET);
    }
}