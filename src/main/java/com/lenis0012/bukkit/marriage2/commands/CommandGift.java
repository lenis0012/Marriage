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
		if(marriage == null) {
			reply(Message.NOT_MARRIED);
			return;
		}

		Player partner = Bukkit.getPlayer(marriage.getOtherPlayer(player.getUniqueId()));
		if(partner == null) {
			reply(Message.PARTNER_NOT_ONLINE);
			return;
		}

		ItemStack item = player.getItemInHand();
		if(item == null || item.getType() == Material.AIR) {
			reply(Message.NO_ITEM);
            return;
		}

		partner.getInventory().addItem(item.clone());
		player.setItemInHand(null);
		reply(Message.ITEM_GIFTED, item.getAmount(), item.getType().toString().toLowerCase());
		reply(partner, Message.GIFT_RECEIVED, item.getAmount(), item.getType().toString().toLowerCase());
	}
}
