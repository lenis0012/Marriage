package com.lenis0012.bukkit.marriage2.listeners;

import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.Relationship;
import com.lenis0012.bukkit.marriage2.config.Settings;
import com.lenis0012.bukkit.marriage2.internal.MarriageCore;
import com.lenis0012.bukkit.marriage2.misc.Cooldown;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class KissListener implements Listener {
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

        MPlayer mp = core.getMPlayer(player);
        if(!mp.isMarried()) {
            return;
        }

        Relationship data = mp.getMarriage();
        if(!clicked.getUniqueId().toString().equalsIgnoreCase(data.getOtherPlayer(player.getUniqueId()).toString())) {
            return;
        }

        if(!cooldown.performCheck(player.getName()) || !cooldown.performCheck(clicked.getName())) {
            return;
        }

        Location l1 = player.getEyeLocation();
        Location l2 = clicked.getEyeLocation();
        spawnParticles(l1, l2);
    }

    private void spawnParticles(Location eye1, Location eye2) {
        Location middle = eye1.clone().add((eye2.getX() - eye1.getX()) / 2, (eye2.getY() - eye1.getY()) / 2, (eye2.getZ() - eye1.getZ()) / 2);
        int min = Settings.KISSES_AMOUNT_MIN.value();
        int max = Settings.KISSES_AMOUNT_MAX.value();
        int amount = min + random.nextInt(max - min + 1);

        eye1.getWorld().spawnParticle(Particle.HEART, middle, amount, 0.3f, 0.3f, 0.3f, 1f);
    }
}
