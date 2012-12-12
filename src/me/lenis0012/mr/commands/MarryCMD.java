package me.lenis0012.mr.commands;

import java.util.logging.Logger;

import me.lenis0012.mr.Marriage;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MarryCMD implements CommandExecutor
{
	private Marriage plugin;
	public MarryCMD(Marriage i) { plugin = i; }
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmnd, String label, String[] args)
	{
		Logger log = plugin.getLogger();
		
		Player player = null;
		if(sender instanceof Player)
		{
			player = (Player)sender;
		}else
		{
			log.info("Command only availeble as player.");
		}
		
		if(args.length == 0)
			InfoCommand.showInfo(player);
		
		else if(args[0].equalsIgnoreCase("accept"))
			AcceptCommand.Accept(player, plugin);
		
		else if(args[0].equalsIgnoreCase("tp"))
			TpCommand.perfrom(player, plugin);
		
		else if(args[0].equalsIgnoreCase("gift"))
			GiftCommand.perfom(player, plugin);
		
		else if(args[0].equalsIgnoreCase("divorce"))
			DivorceCommand.perfrom(player, plugin);
		
		else if(args[0].equalsIgnoreCase("chat"))
			ChatCommand.perform(player, plugin);
		
		else if(args[0].equalsIgnoreCase("home"))
			HomeCommand.perform(player, plugin);
		
		else if(args[0].equalsIgnoreCase("sethome"))
			SethomeCommand.perform(player, plugin);
		
		else if(args[0].equalsIgnoreCase("list"))
			listCommand.perform(player, plugin, args);
		
		else if(args.length == 1)
			MarryCommand.request(player, args, plugin);
		
		return true;
	}

}
