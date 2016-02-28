package com.lenis0012.bukkit.marriage2.commands;

import com.lenis0012.bukkit.marriage2.config.Settings;
import org.bukkit.Bukkit;

import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.Marriage;
import com.lenis0012.bukkit.marriage2.config.Message;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class CommandDivorce extends Command {

	public CommandDivorce(Marriage marriage) {
		super(marriage, "divorce");
		setDescription("Divorce your current partner");
		setExecutionFee(Settings.PRICE_DIVORCE);
	}

	@Override
	public void execute() {
		MPlayer mPlayer = marriage.getMPlayer(player.getUniqueId());
		MPlayer partner = mPlayer.getPartner();
		if(partner == null) {
			reply(Message.NOT_MARRIED);
			return;
		}

		if(!payFee()) return;
		mPlayer.divorce();

		// Clear metadata
		player.removeMetadata("marriedTo", marriage.getPlugin());
		Player target = Bukkit.getPlayer(partner.getUniqueId());
		if(target != null) {
			target.removeMetadata("marriedTo", marriage.getPlugin());
		}

		broadcast(Message.DIVORCED, player.getName(), Bukkit.getOfflinePlayer(partner.getUniqueId()).getName());
	}
}
