package com.lenis0012.bukkit.marriage2.commands;

import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.config.Message;
import com.lenis0012.bukkit.marriage2.config.Settings;
import com.lenis0012.bukkit.marriage2.internal.MarriageCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandDivorce extends Command {

    public CommandDivorce(MarriageCore marriage) {
        super(marriage, "divorce");
        setDescription(Message.COMMAND_DIVORCE.toString());
        setExecutionFee(Settings.PRICE_DIVORCE);
    }

    @Override
    public void execute() {
        MPlayer mPlayer = marriage.getMPlayer(player);
        MPlayer partner = mPlayer.getPartner();
        if(partner == null) {
            reply(Message.NOT_MARRIED);
            return;
        }

        if(!payFee()) return;
        mPlayer.divorce();

        // Clear metadata
        player.removeMetadata("marriedTo", marriage.getPlugin());
        Player target = Bukkit.getPlayer(partner.getUniqueId());
        if(target != null) {
            target.removeMetadata("marriedTo", marriage.getPlugin());
        }

        broadcast(Message.DIVORCED, player.getName(), Bukkit.getOfflinePlayer(partner.getUniqueId()).getName());
    }
}
