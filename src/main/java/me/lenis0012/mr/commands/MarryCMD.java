package me.lenis0012.mr.commands;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MarryCMD implements CommandExecutor {
	private Map<String, CommandBase> commands = new HashMap<String, CommandBase>();
	private MarryCommand marryCommand;
	
	public MarryCMD() {
		this.marryCommand = new MarryCommand();
		commands.put("accept", new AcceptCommand());
		commands.put("chat", new ChatCommand());
		commands.put("divorce", new DivorceCommand());
		commands.put("gift", new GiftCommand());
		commands.put("home", new HomeCommand());
		commands.put("info", new InfoCommand());
		commands.put("list", new ListCommand());
		commands.put("reload", new ReloadCommand());
		commands.put("sethome", new SethomeCommand());
		commands.put("tp", new TpCommand());
		commands.put("seen", new SeenCommand());
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		CommandBase command;
		if(args.length == 0)
			command = commands.get("info");
		else if(commands.containsKey(args[0]))
			command = commands.get(args[0]);
		else
			command = this.marryCommand;
		
		command.execute(sender, args);
		return true;
	}

}
