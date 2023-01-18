package com.lenis0012.bukkit.marriage2.commands;

import com.lenis0012.bukkit.marriage2.MData;
import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.Marriage;
import com.lenis0012.bukkit.marriage2.config.Message;
import com.lenis0012.bukkit.marriage2.config.Settings;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Lennart on 7/9/2015.
 */
public class CommandTeleport extends Command {
    private static final List<Material> UNSAFE_TYPES = Arrays.asList(Material.LAVA, Material.CACTUS);

    public CommandTeleport(Marriage marriage) {
        super(marriage, "tp");
        setDescription(Message.COMMAND_TELEPORT.toString());
        setExecutionFee(Settings.PRICE_TELEPORT);
    }

    @Override
    public void execute() {
        MPlayer mPlayer = marriage.getMPlayer(player);
        MData marriage = mPlayer.getMarriage();
        if(marriage == null) {
            reply(Message.NOT_MARRIED);
            return;
        }

        Player partner = Bukkit.getPlayer(marriage.getOtherPlayer(player.getUniqueId()));
        if(partner == null) {
            reply(Message.PARTNER_NOT_ONLINE);
            return;
        }

        Location destination = partner.getLocation();
        if(player.getGameMode() != GameMode.CREATIVE) {
            destination = getSafeLocation(destination);
        }

        if(destination == null) {
            reply(Message.TELEPORT_UNSAFE);
            return;
        }

        if(!payFee()) return;

        player.teleport(destination);
        reply(Message.TELEPORTED);
        partner.sendMessage(ChatColor.translateAlternateColorCodes('&', Message.TELEPORTED_2.toString()));
    }

    private Location getSafeLocation(Location destination) {
        World world = destination.getWorld();
        Block block = destination.getBlock();
        if(block == null || block.getY() < -64 || block.getY() > world.getMaxHeight()) {
            return null; // Out of bounds, cant teleport to void or from a bizarre height.
        }

        if(isSafeGround(block.getRelative(BlockFace.DOWN))) {
            return destination; // Current destination is valid
        }

        // Find next potentially safe block
        while(!(block.getType().isSolid() || block.isLiquid()) && block.getY() > -64) {
            block = block.getRelative(BlockFace.DOWN);
            if(UNSAFE_TYPES.contains(block.getType())) {
                return null; // Obstructed by unsafe block
            }
        }

        if(!isSafeGround(block)) {
            return null; // Still not safe
        }

        // Safe
        Location target = destination.clone();
        target.setY(block.getY() + 1);
        return target;
    }

    private boolean isSafeGround(Block block) {
        return (block.getType().isSolid() || block.getType() == Material.WATER)
                && !UNSAFE_TYPES.contains(block.getRelative(0, 1, 0).getType())
                && !UNSAFE_TYPES.contains(block.getRelative(0, 2, 0).getType());
    }
}
