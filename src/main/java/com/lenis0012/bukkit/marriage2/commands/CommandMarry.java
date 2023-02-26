package com.lenis0012.bukkit.marriage2.commands;

import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.config.Message;
import com.lenis0012.bukkit.marriage2.config.Settings;
import com.lenis0012.bukkit.marriage2.internal.MarriageCore;
import com.lenis0012.bukkit.marriage2.internal.MarriagePlugin;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandMarry extends Command {

    public CommandMarry(MarriageCore marriage) {
        super(marriage, "marry");
        if(Settings.ENABLE_PRIEST.value()) {
            setDescription(Message.COMMAND_MARRY_PRIEST.toString());
            setUsage("<player1> <player2>");
            setMinArgs(1);
        } else {
            setDescription(Message.COMMAND_MARRY.toString());
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

            MPlayer mp1 = marriage.getMPlayer(player1);
            MPlayer mp2 = marriage.getMPlayer(player2);
            if(mp1.isMarried() || mp2.isMarried()) {
                reply(Message.ALREADY_MARRIED);
                return;
            }
            MPlayer mp = marriage.getMPlayer(player);
            if(!mp.isPriest()) {
                reply(Message.NOT_A_PRIEST);
                return;
            }

            marriage.marry(mp1, mp2);
            broadcast(Message.MARRIED, player1.getName(), player2.getName());
        } else {
            Player target = getArgAsPlayer(-1);

            // Check if player is valid
            if(target == null) {
                reply(Message.PLAYER_NOT_FOUND, getArg(-1));
                return;
            }

            // Check if player is self
            if(target.getName().equalsIgnoreCase(player.getName())) {
                reply(Message.MARRY_SELF);
                return;
            }

            // Check if self married
            MPlayer mPlayer = marriage.getMPlayer(player);
            if(mPlayer.isMarried()) {
                reply(Message.ALREADY_MARRIED);
                return;
            }

            // Check if player married
            MPlayer mTarget = marriage.getMPlayer(target);
            if(mTarget.isMarried()) {
                reply(Message.TARGET_ALREADY_MARRIED, getArg(-1));
                return;
            }

            // Request or accept
            if(mPlayer.isMarriageRequested(target.getUniqueId())) {
                if(getExecutionFee() > 0.0) {
                    EconomyResponse response = MarriagePlugin.dependencies().getEconomyService().withdrawPlayer(target, getExecutionFee());
                    if(!response.transactionSuccess()) {
                        reply(Message.PARTNER_FEE);
                        target.sendMessage(response.errorMessage);
                        return;
                    }
                }

                marriage.marry(mTarget, mPlayer);
                player.setMetadata("marriedTo", new FixedMetadataValue(JavaPlugin.getPlugin(MarriagePlugin.class), target.getName()));
                target.setMetadata("marriedTo", new FixedMetadataValue(JavaPlugin.getPlugin(MarriagePlugin.class), player.getName()));
                broadcast(Message.MARRIED, player.getName(), target.getName());
            } else if(!mTarget.isMarriageRequested(player.getUniqueId())) {
                if(!hasFee()) {
                    reply(Message.INSUFFICIENT_MONEY, MarriagePlugin.dependencies().getEconomyService().format(getExecutionFee()));
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