package me.lenis0012.mr.children;

import org.bukkit.ChatColor;
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
		if(args.length >= 1) {
			if(args[0].equalsIgnoreCase("breed")) {
				Child child = manager.createChild(player);
				child.spawn(player.getLocation());
				FollowCell cell = new FollowCell(child, player);
				child.getMind().addBrainCell(cell);
				player.sendMessage(ChatColor.GREEN+"Your got a baby");
			} else if(args[0].equalsIgnoreCase("stay")) {
				if(hasChild(player)) {
					Child child = getChild(player);
					child.setStaying(true);
					player.sendMessage(ChatColor.GREEN+"Your child is now staying");
				}
			} else if(args[0].equalsIgnoreCase("follow")) {
				if(hasChild(player)) {
					Child child = getChild(player);
					if(child.isStaying())
						child.setStaying(false);
					
					FollowCell cell = new FollowCell(child, player);
					child.getMind().addBrainCell(cell);
					player.sendMessage(ChatColor.GREEN+"Your child is now following you");
				}
			}
		}
		return true;
	}
	
	public boolean hasChild(Player player) {
		ChildManager manager = ChildManager.getInstance();
		String name = player.getName();
		return manager.parents.containsKey(name);
	}
	
	public Child getChild(Player player) {
		ChildManager manager = ChildManager.getInstance();
		String name = player.getName();
		if(hasChild(player)) {
			return manager.parents.get(name);
		}
		return null;
	}
}
