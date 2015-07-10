package com.lenis0012.bukkit.marriage2.listeners;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.lenis0012.bukkit.marriage2.MData;
import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.internal.MarriageCore;
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

public class KissListener implements Listener {
    private final List<String> cooldown = Lists.newArrayList();
    private final MarriageCore core;

    public KissListener(MarriageCore core) {
        this.core = core;
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
                        if(!cooldown.contains(player.getName()) && !cooldown.contains(clicked.getName())) {
                            cooldown.add(player.getName());
                            cooldown.add(clicked.getName());
                            Location l1 = player.getEyeLocation();
                            Location l2 = clicked.getEyeLocation();
                            Location l = l1.clone().add((l2.getX() - l1.getX()) / 2, (l2.getY() - l1.getY()) / 2, (l2.getZ() - l1.getZ()) / 2);

                            PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.HEART, false, (float) l.getX(), (float) l.getY(), (float) l.getZ(), 0.3F, 0.3F, 0.3F, 1F, 7);
                            for(Player p : player.getWorld().getPlayers()) {
                                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
                            }

                            Bukkit.getScheduler().runTaskLater(core.getPlugin(), new Runnable() {
                                @Override
                                public void run() {
                                    cooldown.remove(player.getName());
                                    cooldown.remove(clicked.getName());
                                }
                            }, 40L);
                        }
                    }
                }
            }
        }
    }
}
