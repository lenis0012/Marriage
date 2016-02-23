package com.lenis0012.bukkit.marriage2.commands;

import com.lenis0012.bukkit.marriage2.Marriage;
import com.lenis0012.bukkit.marriage2.internal.MarriageCore;
import com.lenis0012.bukkit.marriage2.misc.update.Updater;
import com.lenis0012.bukkit.marriage2.misc.update.Version;
import org.bukkit.Bukkit;

public class CommandUpdate extends Command {

    public CommandUpdate(Marriage marriage) {
        super(marriage, "update");
        setHidden(true);
        setPermission("marry.update");
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
                    }
                });
            }
        });
    }
}
