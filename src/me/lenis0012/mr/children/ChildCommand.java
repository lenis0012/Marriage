package me.lenis0012.mr.children;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChildCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("This command can only be executed as player");
			return true;
		}
		ChildManager manager = ChildManager.getInstance();
		Player player = (Player)sender;
		Child child = manager.createChild(player);
		child.spawn(player.getLocation());
		FollowCell cell = new FollowCell(child, player);
		child.getMind().addBrainCell(cell);
		return true;
	}

}
