package me.lenis0012.mr.children;

import me.lenis0012.mr.children.thinking.FollowCell;

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
		Owner owner = manager.getOwner(player);
		
		if(!player.hasPermission("marry.child")) {
			player.sendMessage(ChatColor.RED + "No permission");
			return true;
		}
		
		if(args.length >= 1) {
			if(args[0].equalsIgnoreCase("breed")) {
				if(owner.isMarried()) {
					Child child = manager.createChild(owner.getName());
					child.spawn(player.getLocation(), true);
					FollowCell cell = new FollowCell(child, player);
					child.getBrain().addBrainCell(cell);
					player.sendMessage(ChatColor.GREEN+"You got a baby");
				} else
					owner.sendMessage(ChatColor.RED+"You aren't married");
			} else if(args[0].equalsIgnoreCase("stay")) {
				if(hasChild(player)) {
					Child child = getChild(player);
					
					if(child.isSpawned()) {
						this.removeFollowCells(child);
						
						child.setStaying(true);
						player.sendMessage(ChatColor.GREEN+"Your child is now staying");
					} else
						player.sendMessage(ChatColor.RED+"Your child is not spawned");
				} else
					player.sendMessage(ChatColor.RED+"You do not have a child");
			} else if(args[0].equalsIgnoreCase("follow")) {
				if(hasChild(player)) {
					Child child = getChild(player);
					
					if(child.isSpawned()) {
						if(child.isStaying())
							child.setStaying(false);
						
						FollowCell cell = new FollowCell(child, player);
						child.getBrain().addBrainCell(cell);
						player.sendMessage(ChatColor.GREEN+"Your child is now following you");
					} else
						player.sendMessage(ChatColor.RED+"Your child is not spawned");
				} else
					player.sendMessage(ChatColor.RED+"You do not have a child");
			} else if (args[0].equalsIgnoreCase("explore")) {
				if(hasChild(player)) {
					Child child = getChild(player);
					
					if(child.isSpawned()) {
						this.removeFollowCells(child);
						if(child.isStaying())
							child.setStaying(false);
						
						player.sendMessage(ChatColor.GREEN+"Your child is now exploring");
					} else
						player.sendMessage(ChatColor.RED+"Your child is not spawned");
				} else
					player.sendMessage(ChatColor.RED+"You do not have a child");
			} else if (args[0].equalsIgnoreCase("tphere")) {
				if(hasChild(player)) {
					Child child = getChild(player);
					if(child.isSpawned())
						child.deSpawn(true);
					child.spawn(player.getLocation(), true);
					child.setBaby(true);
					player.sendMessage(ChatColor.GREEN+"Your child has teleported to you");
				} else
					player.sendMessage(ChatColor.RED+"You do not have a child");
			}
		}
		return true;
	}
	
	public void removeFollowCells(Child child) {
		child.getBrain().removeBrainCellWithType("follow");
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
