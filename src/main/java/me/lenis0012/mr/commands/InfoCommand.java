package me.lenis0012.mr.commands;


import me.lenis0012.mr.MPlayer;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class InfoCommand extends CommandBase {

	@Override
	public void perform(CommandSender sender, String[] args) {
		ChatColor g = ChatColor.GRAY;
		ChatColor l = ChatColor.GREEN;
		ChatColor r = ChatColor.RED;
		inform(sender, l + "==========-{"+l+" Marriage "+g+"}-==========");
		inform(sender, l + "Version: "+l+plugin.getDescription().getVersion());
		inform(sender, l + "Authors: "+l+plugin.getDescription().getAuthors().toString().replace("[", "").replace("]", ""));
		inform(sender, l + "/marry <player> "+g+"- Marry a player");
		inform(sender, l + "/marry accept "+g+"- Accept a marriage request");
		inform(sender, l + "/marry divorce "+g+"- Divorce your partner");
		inform(sender, l + "/marry list "+g+"- See all married players");
		inform(sender, l + "/marry tp "+g+"- Teleport to your partner");
		inform(sender, l + "/marry gift "+g+"- Gift your partner the item in your hand");
		inform(sender, l + "/marry chat "+g+"- Private chat with your partner");
		inform(sender, l + "/marry sethome "+g+"- Set your marriage home");
		inform(sender, l + "/marry home "+g+"- Go to your marriage home");
		inform(sender, l + "/marry reload"+g+" - Reload all config files");
		inform(sender, l + "Crouch + Right click"+g+" - Kiss your partner");
		
		if(this.isPlayer()) {
			Player player = (Player) sender;
			MPlayer mp = plugin.getMPlayer(player);
			if(mp.isMarried()) {
				inform(sender, "Married: "+l+mp.getPartner());
			}else {
				inform(sender, "Married: "+r+"No");
			}
		}
	}

	@Override
	public String getPermission() {
		return null;
	}

	@Override
	public boolean playersOnly() {
		return false;
	}
}
