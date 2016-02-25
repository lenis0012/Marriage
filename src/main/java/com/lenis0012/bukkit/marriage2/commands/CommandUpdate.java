package com.lenis0012.bukkit.marriage2.commands;

import com.lenis0012.bukkit.marriage2.Marriage;
import com.lenis0012.bukkit.marriage2.config.Settings;
import com.lenis0012.bukkit.marriage2.internal.MarriageCore;
import com.lenis0012.updater.api.Updater;
import com.lenis0012.updater.api.Version;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class CommandUpdate extends Command {

    public CommandUpdate(Marriage marriage) {
        super(marriage, "update");
        setHidden(true);
    }

    @Override
    public void execute() {
        final Updater updater = ((MarriageCore) marriage).getUpdater();
        final Version version = updater.getNewVersion();
        if(version == null) {
            reply("&cUpdater is not enabled!");
            return;
        }

        reply("&aDownloading " + version.getName() + "...");
        Bukkit.getScheduler().runTaskAsynchronously(marriage.getPlugin(), new Runnable() {
            @Override
            public void run() {
                String message = updater.downloadVersion();
                final String response = message == null ? "&aUpdate successful, will be active on reboot." : "&c&lError: &c" + message;
                Bukkit.getScheduler().runTask(marriage.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        reply(response);
                        if(!Settings.ENABLE_CHANGELOG.value()) {
                            return;
                        }

                        ItemStack changelog = updater.getChangelog();
                        if(changelog == null) {
                            reply("&cChangelog isn't available for this version.");
                            return;
                        }

                        ItemStack inHand = player.getItemInHand();
                        player.setItemInHand(changelog);
                        if(inHand != null) {
                            player.getInventory().addItem(inHand);
                        }

                        reply("&llenis> &bCheck my changelog out! (I put it in your hand)");
                        player.updateInventory();
                    }
                });
            }
        });
    }
}
