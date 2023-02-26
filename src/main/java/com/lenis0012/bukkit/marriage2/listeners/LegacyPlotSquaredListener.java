package com.lenis0012.bukkit.marriage2.listeners;

import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.object.Plot;
import com.lenis0012.bukkit.marriage2.Relationship;
import com.lenis0012.bukkit.marriage2.events.PlayerDivorceEvent;
import com.lenis0012.bukkit.marriage2.events.PlayerMarryEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.UUID;

public class LegacyPlotSquaredListener implements Listener {
    private final PS plotSquared;

    public LegacyPlotSquaredListener() {
        this.plotSquared = PS.get();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMarry(PlayerMarryEvent event) {
        final UUID player = event.getRequesing().getUniqueId();
        final UUID partner = event.getRequested().getUniqueId();

        for(Plot plot : plotSquared.getPlots(player)) {
            if(plot.getTrusted().contains(partner)) {
                continue;
            }
            plot.addTrusted(partner);
        }

        for(Plot plot : plotSquared.getPlots(partner)) {
            if(plot.getTrusted().contains(player)) {
                continue;
            }
            plot.addTrusted(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDivorce(PlayerDivorceEvent event) {
        final Relationship marriage = event.getMarriage();
        final UUID player = marriage.getPlayer1Id();
        final UUID partner = marriage.getPllayer2Id();

        for(Plot plot : plotSquared.getPlots(player)) {
            if(!plot.getTrusted().contains(partner)) {
                continue;
            }
            plot.removeTrusted(partner);
        }

        for(Plot plot : plotSquared.getPlots(partner)) {
            if(!plot.getTrusted().contains(player)) {
                continue;
            }
            plot.removeTrusted(player);
        }
    }
}
