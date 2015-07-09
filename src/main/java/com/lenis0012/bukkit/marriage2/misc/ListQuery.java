package com.lenis0012.bukkit.marriage2.misc;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import com.lenis0012.bukkit.marriage2.MData;
import com.lenis0012.bukkit.marriage2.internal.MarriagePlugin;

public class ListQuery {
	private final int pages;
	private final int page;
	private final List<MData> marriages;
	
	public ListQuery(int pages, int page, List<MData> marriages) {
		this.pages = pages;
		this.page = page;
		this.marriages = marriages;
	}
	
	public void send(final CommandSender to) {
		new BukkitRunnable() {
			@Override
			public void run() {
				to.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "Married players:");
				to.sendMessage(ChatColor.GOLD + "Page " + (page + 1) + "/" + pages);
				for(MData data : marriages) {
					OfflinePlayer player1 = Bukkit.getOfflinePlayer(data.getPlayer1Id());
					OfflinePlayer player2 = Bukkit.getOfflinePlayer(data.getPllayer2Id());
					to.sendMessage(ChatColor.GREEN + player1.getName() + " + " + player2.getName());
				}
			}
		}.runTask(MarriagePlugin.getInstance().getPlugin());
	}

	public int getPages() {
		return pages;
	}

	public int getPage() {
		return page;
	}

	public List<MData> getMarriages() {
		return marriages;
	}
}