package com.lenis0012.bukkit.marriage.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Check BukkitDev for updates.
 * Reads files.rss and searches for first name of item.
 * Does NOT download any updates, just checks is available.
 * This can be turned off in config.yml: update-checker
 * 
 * @author lenis0012
 */
public class UpdateChecker implements Runnable {
	private final String PROJECT_URL;
	private final String PLUGIN_VERSION;
	private boolean HAS_UPDATE;
	private String error;
	
	public UpdateChecker(Plugin plugin, String urlPart) {
		PLUGIN_VERSION = plugin.getDescription().getVersion();
		PROJECT_URL = "http://dev.bukkit.org/bukkit-mods/" + urlPart + "/files.rss";
		if(plugin.getConfig().getBoolean("update-checker", true)) {
			Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, 0, 6000);
		}
	}
	
	public void run() {
		try {
			URL url = new URL(PROJECT_URL);
			InputStream input = url.openStream();
			InputStreamReader isr = new InputStreamReader(input);
			BufferedReader reader = new BufferedReader(isr);
			String title = null;
			boolean inItemList = false;
			String line = null;
			while((line = reader.readLine()) != null) {
				if(!inItemList)
					inItemList = line.contains("<item>");
				
				if(inItemList && line.contains("<title>")) {
					title = this.find(line, '>', '<');
					break;
				}
			}
			
			reader.close();
			if(title != null) {
				int oldVersion = this.parseInt(PLUGIN_VERSION);
				int newVersion = this.parseInt(title);
				String oldVStr = Integer.toString(oldVersion);
				String newVStr = Integer.toString(newVersion);
				
				//Lenght fix, 1.2 > 1.1.1 (12 & 111)
				while(oldVStr.length() < newVStr.length()) {
					oldVStr += "0";
				} while(newVStr.length() < oldVStr.length()) {
					newVStr += "0";
				}
				
				oldVersion = Integer.parseInt(oldVStr);
				newVersion = Integer.parseInt(newVStr);
				HAS_UPDATE = oldVersion < newVersion;
				this.error = "";
			} else
				this.error = "Failed to parse file version, invalid response.";
		} catch (Exception e) {
			this.error = e.getMessage();
		}
		
		System.out.println(this.error);
	}
	
	/**
	 * Loops through all chars in a string and returns the string between 2 chars.
	 * Usualy used to get >Target text<
	 * 
	 * @param from Original full string.
	 * @param begin Char to sart at.
	 * @param end Char to end at.
	 * @return Text from original between chars.
	 */
	private String find(String from, char begin, char end) {
		int i = from.indexOf(begin);
		StringBuilder builder = new StringBuilder();
		while(from.indexOf(end) < i) {
			from = from.replaceFirst(Character.toString(end), "");
		}
		
		for(int j = i; j < from.indexOf(end); j++) {
			builder.append(from.charAt(j));
		}
		
		return builder.toString();
	}
	
	/**
	 * Return all digits from a string.
	 * 
	 * @param toParse String to parse.
	 * @return ALl digits from string as int.
	 */
	private int parseInt(String toParse) {
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < toParse.length(); i++) {
			char c = toParse.charAt(i);
			if(Character.isDigit(c)) {
				builder.append(c);
			}
		}
		
		String rawInt = builder.toString();
		return rawInt.length() <= 0 ? 0 : Integer.parseInt(rawInt);
	}
	
	/**
	 * Check if updates were found.
	 * 
	 * @return Updates found?
	 */
	public boolean hasUpdate() {
		return HAS_UPDATE;
	}
	
	/**
	 * Get error message, return NULL when not yet started.
	 * And empty when no error.
	 * 
	 * @return Error message
	 */
	public String getErrorMessage() {
		return this.error;
	}
}