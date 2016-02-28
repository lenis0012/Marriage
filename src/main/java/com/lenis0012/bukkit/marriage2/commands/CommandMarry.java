package com.lenis0012.bukkit.marriage2.commands;

import com.lenis0012.bukkit.marriage2.config.Settings;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.Marriage;
import com.lenis0012.bukkit.marriage2.config.Message;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.UUID;

public class CommandMarry extends Command {

	public CommandMarry(Marriage marriage) {
		super(marriage, "marry");
        if(Settings.ENABLE_PRIEST.value()) {
            setDescription("Marry 2 players with eachother.");
            setUsage("<player1> <player2>");
            setMinArgs(1);
        } else {
            setDescription("Request a marriage with another player.");
            setExecutionFee(Settings.PRICE_MARRY);
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
            if(player1 == null) {
                reply(Message.PLAYER_NOT_FOUND, getArg(-1));
                return;
            } else if(player2 == null) {
                reply(Message.PLAYER_NOT_FOUND, getArg(0));
                return;
            }

            MPlayer mp1 = marriage.getMPlayer(player1.getUniqueId());
            MPlayer mp2 = marriage.getMPlayer(player2.getUniqueId());
            if(mp1.isMarried() || mp2.isMarried()) {
                reply(Message.ALREADY_MARRIED);
                return;
            }
            MPlayer mp = marriage.getMPlayer(player.getUniqueId());
            if(!mp.isPriest()) {
                reply(Message.NOT_A_PRIEST);
                return;
            }

            marriage.marry(mp1, mp2);
            broadcast(Message.MARRIED, player1.getName(), player2.getName());
        } else {
            Player target = getArgAsPlayer(-1);

            // Check if player is valid
            if (target == null) {
                reply(Message.PLAYER_NOT_FOUND, getArg(-1));
                return;
            }

            // Check if player is self
            if (target.getName().equalsIgnoreCase(player.getName())) {
                reply(Message.MARRY_SELF);
                return;
            }

            // Check if self married
            MPlayer mPlayer = marriage.getMPlayer(player.getUniqueId());
            if (mPlayer.isMarried()) {
                reply(Message.ALREADY_MARRIED);
                return;
            }

            // Check if player married
            MPlayer mTarget = marriage.getMPlayer(target.getUniqueId());
            if (mTarget.isMarried()) {
                reply(Message.TARGET_ALREADY_MARRIED, getArg(-1));
                return;
            }

            // Request or accept
            if (mPlayer.isMarriageRequested(target.getUniqueId())) {
                if(getExecutionFee() > 0.0) {
                    EconomyResponse response = marriage.dependencies().getEconomyService().withdrawPlayer(target, getExecutionFee());
                    if(!response.transactionSuccess()) {
                        reply(Message.PARTNER_FEE);
                        target.sendMessage(response.errorMessage);
                        return;
                    }
                }

                marriage.marry(mTarget, mPlayer);
                player.setMetadata("marriedTo", new FixedMetadataValue(marriage.getPlugin(), target.getName()));
                target.setMetadata("marriedTo", new FixedMetadataValue(marriage.getPlugin(), player.getName()));
                broadcast(Message.MARRIED, player.getName(), target.getName());
            } else if(!mTarget.isMarriageRequested(player.getUniqueId())) {
                if(!hasFee()) {
                    reply(Message.INSUFFICIENT_MONEY, marriage.dependencies().getEconomyService().format(getExecutionFee()));
                    return;
                }

                mTarget.requestMarriage(player.getUniqueId());
                target.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(Message.MARRIAGE_REQUESTED.toString(), player.getName(), player.getName())));
                reply(Message.REQUEST_SENT, target.getName());
            } else {
                // Already requested, cooling down.
                reply(Message.COOLDOWN);
            }
        }
	}
}