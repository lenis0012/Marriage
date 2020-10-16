package com.lenis0012.bukkit.marriage2.listeners;

import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.Marriage;
import com.lenis0012.bukkit.marriage2.config.Message;
import com.lenis0012.bukkit.marriage2.config.Settings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.projectiles.ProjectileSource;

public class PlayerListener implements Listener {
    private final Marriage marriage;

    public PlayerListener(Marriage marriage) {
        this.marriage = marriage;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        final Entity e0 = event.getEntity();
        final Entity e1 = event.getDamager();
        // Verify damaged entity is player
        if(!(e0 instanceof Player)) {
            return;
        } if(!(e1 instanceof Player) && !(e1 instanceof Projectile)) {
            return;
        }

        // Verify damager is player
        final Player player = (Player) e0;
        final Player damager;
        if(e1 instanceof Player) {
            damager = (Player) e1;
        } else {
            Projectile projectile = (Projectile) e1;
            final ProjectileSource e3 = projectile.getShooter();
            if(e3 == null || !(e3 instanceof Player)) {
                return;
            }
            damager = (Player) e3;
        }

        // Verify marriage
        MPlayer mplayer = marriage.getMPlayer(player);
        if(!mplayer.isMarried() || mplayer.getMarriage().getOtherPlayer(player.getUniqueId()) != damager.getUniqueId()) {
            return;
        }

        // Verify pvp setting
        if(mplayer.getMarriage().isPVPEnabled()) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerGainExp(PlayerExpChangeEvent event) {
        if(!Settings.EXP_BOOST_ENABLED.value()) {
            return;
        }

        final int gained = event.getAmount();
        if(gained <= 0) {
            return;
        }

        final Player player = event.getPlayer();
        final MPlayer mplayer = marriage.getMPlayer(player);
        if(!mplayer.isMarried()) {
            return;
        }

        MPlayer mpartner = mplayer.getPartner();
        Player partner = Bukkit.getPlayer(mpartner.getUniqueId());
        if(partner == null || !partner.isOnline()) {
            return;
        }

        if(!partner.getWorld().equals(player.getWorld())) {
            return;
        }

        if(partner.getLocation().distanceSquared(partner.getLocation()) > Settings.EXP_BOOST_DISTANCE.value() * Settings.EXP_BOOST_DISTANCE.value()) {
            return;
        }

        event.setAmount((int) Math.round(gained * Settings.EXP_BOOST_MULTIPLIER.value()));
        final int bonus = event.getAmount() - gained;
        if(bonus > 0 && Settings.EXP_BOOST_ANNOUNCE.value()) {
            Message.BONUS_EXP.send(player, bonus);
        }
    }
}
