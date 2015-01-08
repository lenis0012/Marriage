package com.lenis0012.bukkit.marriage2.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.lenis0012.bukkit.marriage2.MData;
import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.Marriage;
import com.lenis0012.bukkit.marriage2.config.Message;

public class CommandGift extends Command {

	public CommandGift(Marriage marriage) {
		super(marriage, "gift");
		setDescription("Gift the item(s) you currently hold");
	}

	@Override
	public void execute() {
		MPlayer mPlayer = marriage.getMPlayer(player.getUniqueId());
		MData marriage = mPlayer.getMarriage();
		if(marriage != null) {
			Player partner = Bukkit.getPlayer(marriage.getOtherPlayer(player.getUniqueId()));
			if(partner != null) {
				ItemStack item = player.getItemInHand();
				if(item != null && item.getType() != Material.AIR) {
					partner.getInventory().addItem(item.clone());
					player.setItemInHand(null);
					reply(Message.ITEM_GIFTED, item.getAmount(), item.getType().toString().toLowerCase());
					reply(partner, Message.GIFT_RECEIVED, item.getAmount(), item.getType().toString().toLowerCase());
				} else {
					reply(Message.NO_ITEM);
				}
			} else {
				reply(Message.PARTNER_NOT_ONLINE);
			}
		} else {
			reply(Message.NOT_MARRIED);
		}
	}
}
