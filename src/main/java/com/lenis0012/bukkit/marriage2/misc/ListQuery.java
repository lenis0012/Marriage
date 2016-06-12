package com.lenis0012.bukkit.marriage2.misc;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lenis0012.bukkit.marriage2.Gender;
import com.lenis0012.bukkit.marriage2.MData;
import com.lenis0012.bukkit.marriage2.config.Settings;
import com.lenis0012.bukkit.marriage2.internal.MarriagePlugin;
import com.lenis0012.bukkit.marriage2.internal.data.DataManager;
import com.lenis0012.bukkit.marriage2.internal.data.MarriagePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ListQuery {
	private static final JsonParser JSON_PARSER = new JsonParser();

	private final int pages;
	private final int page;
	private final List<MData> marriages;
	private final Map<UUID, String> names = Maps.newHashMap();
	
	public ListQuery(DataManager db, int pages, int page, List<MData> marriages) {
		this.pages = pages;
		this.page = page;
		this.marriages = marriages;
		for(MData marriage : marriages) {
			names.put(marriage.getPlayer1Id(), getNameFormat(db, marriage.getPlayer1Id()));
			names.put(marriage.getPllayer2Id(), getNameFormat(db, marriage.getPllayer2Id()));
		}
	}
	
	public void send(final CommandSender to) {
		new BukkitRunnable() {
			@Override
			public void run() {
				to.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "Married players:");
				to.sendMessage(ChatColor.GOLD + "Page " + (page + 1) + "/" + pages);
				if(Settings.GENDER_IN_LIST.value()) {
					to.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bmale &f- &dfemale &f- &7unknown"));
				}
				for(MData data : marriages) {
					to.sendMessage(names.get(data.getPlayer1Id()) + ChatColor.WHITE + " + " + names.get(data.getPllayer2Id()));
				}
			}
		}.runTask(MarriagePlugin.getCore().getPlugin());
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

	public static String getNameFormat(DataManager db, UUID userId) {
		String name = getName(db, userId);
		if(name == null) {
			return ChatColor.GREEN + "???";
		}

		ChatColor color = ChatColor.GREEN;
		if(Settings.GENDER_IN_LIST.value()) {
			MarriagePlayer mp = db.loadPlayer(userId);
			Gender gender = mp == null ? Gender.UNKNOWN : mp.getGender();
			switch(gender) {
				case MALE:
					color = ChatColor.AQUA;
					break;
				case FEMALE:
					color = ChatColor.LIGHT_PURPLE;
					break;
				case UNKNOWN:
					color = ChatColor.GRAY;
					break;
			}
		}

		return color + getName(db, userId);
	}

	public static String getName(DataManager db, UUID userId) {
		// local uuid cache
		OfflinePlayer op = Bukkit.getOfflinePlayer(userId);
		if(op != null && op.getName() != null) {
			return op.getName();
		}

		// local database
		MarriagePlayer mp = db.loadPlayer(userId);
		if(mp.getLastName() != null) {
			return mp.getLastName();
		}

		// Last attempt, fetch from mojang.
		return nameFromMojang(userId);
	}

	public static String nameFromMojang(UUID uuid) {
		try {
			URL url = new URL("  https://api.mojang.com/user/profiles/" + uuid.toString().replace("-", "") + "/names");
			URLConnection connection = url.openConnection();
			connection.addRequestProperty("User-Agent", "Mozilla/4.0");
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder builder = new StringBuilder();
			String line;
			while((line = reader.readLine()) != null) {
				builder.append(line);
			}

			JsonArray entries = JSON_PARSER.parse(builder.toString()).getAsJsonArray();
			if(entries.size() == 0) return null; // Fail
			JsonObject lastEntry = entries.get(entries.size() - 1).getAsJsonObject();
			return lastEntry.get("name").getAsString();
		} catch(Exception e) {
			return null; // Complete failure
		}
	}
}