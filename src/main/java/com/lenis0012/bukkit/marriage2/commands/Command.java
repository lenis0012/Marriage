package com.lenis0012.bukkit.marriage2.commands;

import com.google.common.collect.Lists;
import com.lenis0012.bukkit.marriage2.config.Message;
import com.lenis0012.bukkit.marriage2.config.Permissions;
import com.lenis0012.bukkit.marriage2.config.Settings;
import com.lenis0012.bukkit.marriage2.internal.Dependencies;
import com.lenis0012.bukkit.marriage2.internal.MarriageCore;
import com.lenis0012.pluginutils.config.mapping.ConfigOption;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class Command {
    protected final MarriageCore marriage;
    private final String[] aliases;

    private String description = "No description available";
    private String usage = "";
    private int minArgs = 0;
    private Permissions permission = null;
    private boolean allowConsole = false;
    private boolean hidden = false;
    private double executionFee = 0.0;

    protected CommandSender sender;
    protected Player player;
    private String[] args;

    public Command(MarriageCore marriage, String command, String... aliases) {
        this.marriage = marriage;
        this.aliases = Lists.asList(command, aliases).toArray(new String[0]);
        this.permission = Permissions.getByNode("marry." + command);
    }

    public abstract void execute();

    public void prepare(CommandSender sender, String[] args) {
        this.sender = sender;
        this.player = sender instanceof Player ? (Player) sender : null;
        this.args = args;
    }

    protected String getArg(int index) {
        return args[index + 1];
    }

    protected int getArgAsInt(int index) {
        return getArgAsInt(index, 0);
    }

    protected Player getArgAsPlayer(int index) {
        String name = getArg(index);
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.getName().equalsIgnoreCase(name)) {
                return player;
            }
        }
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.getName().toLowerCase().contains(name.toLowerCase())) {
                return player;
            }
        }

        return null;
    }

    protected int getArgLength() {
        return args.length - 1;
    }

    protected int getArgAsInt(int index, int def) {
        try {
            return Integer.parseInt(getArg(index));
        } catch(Exception e) {
            return def;
        }
    }

    /**
     * Reply to the command sender with a specified formatted message.
     *
     * @param message Message to send.
     * @param args    Formatting arguments
     */
    protected void reply(Message message, Object... args) {
        reply(sender, message, args);
    }

    /**
     * Reply to the command sender with a formatted message.
     *
     * @param message Message to send.
     * @param args    Formatting arguments
     */
    protected void reply(String message, Object... args) {
        reply(sender, message, args);
    }

    protected void reply(CommandSender sender, Message message, Object... args) {
        reply(sender, message.toString(), args);
    }

    protected void reply(CommandSender sender, String message, Object... args) {
        if (message.isEmpty()) return;
        message = ChatColor.translateAlternateColorCodes('&', String.format(message, args));
        sender.sendMessage(message);
    }

    protected void broadcast(Message message, Object... args) {
        broadcast(message.toString(), args);
    }

    protected void broadcast(String message, Object... args) {
        if (message.isEmpty()) return;
        message = ChatColor.translateAlternateColorCodes('&', String.format(message, args));
        Bukkit.broadcastMessage(message);
    }

    public Permissions getPermission() {
        return permission;
    }

    public void setPermission(Permissions permission) {
        this.permission = permission;
    }

    public String[] getAliases() {
        return aliases;
    }

    public String getDescription() {
        return description;
    }

    public String getUsage() {
        return usage;
    }

    public int getMinArgs() {
        return minArgs;
    }

    public boolean isAllowConsole() {
        return allowConsole;
    }

    protected void setDescription(String description) {
        this.description = description;
    }

    protected void setUsage(String usage) {
        this.usage = usage;
    }

    protected void setMinArgs(int minArgs) {
        this.minArgs = minArgs;
    }

    protected void setAllowConsole(boolean allowConsole) {
        this.allowConsole = allowConsole;
    }

    protected boolean isHidden() {
        return hidden;
    }

    protected void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    protected void setExecutionFee(ConfigOption<Double> setting) {
        if(!Settings.ECONOMY_ENABLED.value() || !Settings.ECONOMY_SHOW_PRICE.value()) {
            return;
        }

        Dependencies dependencies = marriage.dependencies();
        if(!dependencies.isEconomyEnabled()) {
            return;
        }

        this.executionFee = setting.value();
    }

    protected double getExecutionFee() {
        return executionFee;
    }

    protected boolean hasFee() {
        if(executionFee <= 0 || player == null) return true; // Success!
        return marriage.dependencies().getEconomyService().has(player, executionFee);
    }

    protected boolean payFee() {
        if(executionFee <= 0 || player == null) return true; // Success!

        if(marriage.dependencies().getEconomyService().has(player, executionFee)) {
            EconomyResponse response = marriage.dependencies().getEconomyService().withdrawPlayer(player, executionFee);
            reply(Message.PAID_FEE, marriage.dependencies().getEconomyService().format(executionFee));
            return response.transactionSuccess();
        } else {
            reply(Message.INSUFFICIENT_MONEY, marriage.dependencies().getEconomyService().format(executionFee));
            return false;
        }
    }
}