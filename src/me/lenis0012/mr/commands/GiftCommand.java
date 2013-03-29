package me.lenis0012.mr.commands;

import me.lenis0012.mr.MPlayer;
import me.lenis0012.mr.lang.Messages;
import me.lenis0012.mr.util.EcoUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiftCommand extends CommandBase {

	@Override
	public void perform(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		MPlayer mp = plugin.getMPlayer(player);
		Player op = Bukkit.getServer().getPlayer(mp.getPartner());
		
		if(!mp.isMarried()) {
			error(player, Messages.NO_PARTNER);
			return;
		}
		
		if(op == null || !op.isOnline()) {
			error(player, Messages.NOT_ONLINE);
			return;
		}
		
		if(plugin.eco) {
			double a = EcoUtil.getPriceFromConfig("gift");
			if(a != 0.0) {
				if(EcoUtil.withrawMoneyIfEnough(player, a)) {
					return;
				}
			}
		}
		
		ItemStack it = player.getItemInHand();
		if(it != null) {
			if(it.getType() != Material.AIR) {
				inform(player, Messages.GIFT_SENT);
				String item = it.getType().toString().toLowerCase();
				String msg = Messages.GIFT_RECEIVED.replace("{ITEM}", item);
				op.sendMessage(ChatColor.GREEN + msg);
				op.getInventory().addItem(it);
				player.setItemInHand(null);
			}else
				error(player, Messages.INVALID_ITEM);
		}else
			error(player, Messages.INVALID_ITEM);
	}

	@Override
	public String getPermission() {
		return "marry.gift";
	}

	@Override
	public boolean playersOnly() {
		return true;
	}
}
