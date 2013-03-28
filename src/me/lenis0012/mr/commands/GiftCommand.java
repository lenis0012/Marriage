package me.lenis0012.mr.commands;

import me.lenis0012.mr.MPlayer;
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
			player.sendMessage(ChatColor.RED + "You dont have a partner.");
			return;
		}
		
		if(op == null || !op.isOnline()) {
			player.sendMessage(ChatColor.RED + "Your partner is not online");
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
				player.sendMessage(ChatColor.GREEN + "Gift sended");
				String item = it.getType().toString().toLowerCase();
				op.sendMessage(ChatColor.GREEN + "You got a '"+item+"' from your partner");
				op.getInventory().addItem(it);
				player.setItemInHand(null);
			}else
				player.sendMessage(ChatColor.RED+"Invalid item in your hand");
		}else
			player.sendMessage(ChatColor.RED+"Invalid item in your hand");
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
