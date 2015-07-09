package com.lenis0012.bukkit.marriage2.commands;

import com.lenis0012.bukkit.marriage2.config.Settings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.Marriage;
import com.lenis0012.bukkit.marriage2.config.Message;

public class CommandMarry extends Command {
	
	public CommandMarry(Marriage marriage) {
		super(marriage, "marry");
        if(Settings.ENABLE_PRIEST.value()) {
            setDescription("Marry 2 players with eachother.");
            setUsage("<player1> <player2>");
            setMinArgs(1);
        } else {
            setDescription("Request a marriage with another player.");
            setUsage("<player>");
            setMinArgs(0);
        }

		setAllowConsole(false);
	}
	
	@Override
	public void execute() {
        if(Settings.ENABLE_PRIEST.value()) {
            Player player1 = getArgAsPlayer(-1);
            Player player2 = getArgAsPlayer(0);
            MPlayer mp1 = marriage.getMPlayer(player1.getUniqueId());
            MPlayer mp2 = marriage.getMPlayer(player2.getUniqueId());
            MPlayer mp = marriage.getMPlayer(player.getUniqueId());

        } else {
            Player target = getArgAsPlayer(-1);
            if (target != null) {
                if (target.getName().equalsIgnoreCase(player.getName())) {
                    reply(Message.MARRY_SELF);
                } else {
                    MPlayer mPlayer = marriage.getMPlayer(player.getUniqueId());
                    if (!mPlayer.isMarried()) {
                        MPlayer mTarget = marriage.getMPlayer(target.getUniqueId());
                        if (!mTarget.isMarried()) {
                            if (mPlayer.isMarriageRequested(target.getUniqueId())) {
                                marriage.marry(mPlayer, mTarget);
                                broadcast(Message.MARRIED, player.getName(), target.getName());
                            } else {
                                mTarget.requestMarriage(player.getUniqueId());
                                target.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(Message.MARRIAGE_REQUESTED.toString(), player.getName(), player.getName())));
                                reply(Message.REQUEST_SENT, target.getName());
                            }
                        } else {
                            reply(Message.TARGET_ALREADY_MARRIED, getArg(-1));
                        }
                    } else {
                        reply(Message.ALREADY_MARRIED);
                    }
                }
            } else {
                reply(Message.PLAYER_NOT_FOUND, getArg(-1));
            }
        }
	}
}