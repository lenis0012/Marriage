package me.lenis0012.mr.commands;

import me.lenis0012.mr.MPlayer;
import me.lenis0012.mr.Marriage;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class InfoCommand
{
	public static void showInfo(Player player, Marriage plugin)
	{
		ChatColor g = ChatColor.GRAY;
		ChatColor l = ChatColor.GREEN;
		ChatColor r = ChatColor.RED;
		player.sendMessage(g +"==========-{"+l+" Marriage "+g+"}-==========");
		player.sendMessage(g+"Version: "+l+plugin.getDescription().getVersion());
		player.sendMessage(g+"Authors: "+l+plugin.getDescription().getAuthors().toString().replace("[", "").replace("]", ""));
		player.sendMessage(l+"/marry <player> "+g+"- Marry a player");
		player.sendMessage(l+"/marry accept "+g+"- Accept a marriage request");
		player.sendMessage(l+"/marry divorce "+g+"- Divorce your partner");
		player.sendMessage(l+"/marry list "+g+"- See all married players");
		player.sendMessage(l+"/marry tp "+g+"- Teleport to your partner");
		player.sendMessage(l+"/marry gift "+g+"- Gift your partner the item in your hand");
		player.sendMessage(l+"/marry chat "+g+"- Private chat with your partner");
		player.sendMessage(l+"/marry sethome "+g+"- Set your marriage home");
		player.sendMessage(l+"/marry home "+g+"- Go to your marriage home");
		
		MPlayer mp = new MPlayer(player);
		if(mp.isMarried())
		{
			player.sendMessage(g+"Married: "+l+mp.getPartner());
		}else
		{
			player.sendMessage(g+"Married: "+r+"No");
		}
	}
}
