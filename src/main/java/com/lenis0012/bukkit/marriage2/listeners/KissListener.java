package com.lenis0012.bukkit.marriage2.listeners;

import com.lenis0012.bukkit.marriage2.MData;
import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.config.Settings;
import com.lenis0012.bukkit.marriage2.internal.MarriageCore;
import com.lenis0012.bukkit.marriage2.misc.Cooldown;
import com.lenis0012.pluginutils.misc.Reflection;
import com.lenis0012.pluginutils.modules.packets.Packet;
import com.lenis0012.pluginutils.modules.packets.PacketModule;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.lang.reflect.Method;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class KissListener implements Listener {
    private final Method GET_PARTICLE_BY_ID = Reflection.getNMSMethod("EnumParticle", "a", int.class);

    private final Cooldown<String> cooldown;
    private final MarriageCore core;
    private final Random random = new Random();

    public KissListener(MarriageCore core) {
        this.core = core;
        this.cooldown = new Cooldown<>(Settings.COOLDOWN_KISS.value(), TimeUnit.SECONDS);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        if(!Settings.KISSES_ENABLED.value()) return; // Disabled

        final Player player = event.getPlayer();
        Entity e = event.getRightClicked();
        if(!(e instanceof Player)) {
            return;
        }

        final Player clicked = (Player) e;
        if(!player.isSneaking() || !clicked.isSneaking()) {
            return;
        }

        MPlayer mp = core.getMPlayer(player.getUniqueId());
        if(!mp.isMarried()) {
            return;
        }

        MData data = mp.getMarriage();
        if(!clicked.getUniqueId().toString().equalsIgnoreCase(data.getOtherPlayer(player.getUniqueId()).toString())) {
            return;
        }

        if(!cooldown.performCheck(player.getName()) || !cooldown.performCheck(clicked.getName())) {
            return;
        }

        Location l1 = player.getEyeLocation();
        Location l2 = clicked.getEyeLocation();
        sendPacket(l1, l2);
    }

    private void sendPacket(Location eye1, Location eye2) {
        Location l = eye1.clone().add((eye2.getX() - eye1.getX()) / 2, (eye2.getY() - eye1.getY()) / 2, (eye2.getZ() - eye1.getZ()) / 2);
        int min = Settings.KISSES_AMOUNT_MIN.value();
        int max = Settings.KISSES_AMOUNT_MAX.value();
        int amount = min + random.nextInt(max - min + 1);

        PacketModule module = core.getPlugin().getModule(PacketModule.class);
        Packet packet = module.createPacket("PacketPlayOutWorldParticles");
        packet.write("a", Reflection.invokeMethod(GET_PARTICLE_BY_ID, null, 34));
        packet.write("b", (float) l.getX());
        packet.write("c", (float) l.getY());
        packet.write("d", (float) l.getZ());
        packet.write("e", 0.3F);
        packet.write("f", 0.3F);
        packet.write("g", 0.3F);
        packet.write("h", 1F);
        packet.write("i", amount);
        module.broadcastPacket(l.getWorld(), packet);
    }
}
