package com.lenis0012.bukkit.marriage2.listeners;

import com.google.common.collect.Lists;
import com.lenis0012.bukkit.marriage2.MData;
import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.config.Settings;
import com.lenis0012.bukkit.marriage2.internal.MarriageCore;
import com.lenis0012.bukkit.marriage2.misc.Cooldown;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class KissListener implements Listener {
    private final Cooldown<String> cooldown;
    private final MarriageCore core;

    public KissListener(MarriageCore core) {
        this.core = core;
        this.cooldown = new Cooldown<>(Settings.COOLDOWN_KISS.value(), TimeUnit.SECONDS);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        final Player player = event.getPlayer();
        Entity e = event.getRightClicked();
        if(e instanceof Player) {
            final Player clicked = (Player) e;
            if(player.isSneaking() && clicked.isSneaking()) {
                MPlayer mp = core.getMPlayer(player.getUniqueId());
                if(mp.isMarried()) {
                    MData data = mp.getMarriage();
                    if(clicked.getUniqueId().toString().equalsIgnoreCase(data.getOtherPlayer(player.getUniqueId()).toString())) {
                        if(cooldown.performCheck(player.getName()) && cooldown.performCheck(clicked.getName())) {
                            Location l1 = player.getEyeLocation();
                            Location l2 = clicked.getEyeLocation();
                            Location l = l1.clone().add((l2.getX() - l1.getX()) / 2, (l2.getY() - l1.getY()) / 2, (l2.getZ() - l1.getZ()) / 2);

                            PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.HEART, false, (float) l.getX(), (float) l.getY(), (float) l.getZ(), 0.3F, 0.3F, 0.3F, 1F, 7);
                            for(Player p : player.getWorld().getPlayers()) {
                                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
                            }
                        }
                    }
                }
            }
        }
    }
}
