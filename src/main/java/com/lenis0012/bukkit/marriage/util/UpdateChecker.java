package com.lenis0012.bukkit.marriage.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Check BukkitDev for updates.
 * This can be turned off in config.yml: update-checker
 * 
 * @author lenis0012
 */
public class UpdateChecker implements Runnable {
	//API info
	private final String API_HOST = "https://api.curseforge.com";
	private final String API_QUERY_FILES = "/servermods/files?projectIds=";
	private final String API_QUERY_SEARCH = "/servermods/projects?search=";
	private final String API_ID_VALUE = "id";
	private final String API_SLUG_VALUE = "slug";
	private final String API_NAME_VALUE = "name";
	private final String API_GAME_VERSION_VALUE = "gameVersion";
	private String API_KEY;
	
	//Project info
	private int PROJECT_ID;
	private String PROJECT_SLUG;
	private String CURRENT_VERSION;
	private String NEW_VERSION;
	private String NEW_BUKKIT_VERSION;
	private String NEW_MC_VERSION;
	private boolean HAS_UPDATE;
	private String error;
	
	public UpdateChecker(Plugin plugin) {
		this(plugin, plugin.getName().toLowerCase());
	}
	
	public UpdateChecker(Plugin plugin, String slug) {
		this(plugin, slug, null);
	}
	
	public UpdateChecker(Plugin plugin, String slug, String apiKey) {
		PROJECT_ID = 0;
		PROJECT_SLUG = slug;
		API_KEY = apiKey;
		CURRENT_VERSION = plugin.getDescription().getVersion();
		if(plugin.getConfig().getBoolean("update-checker", true)) {
			Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, 0, 6000);
		}
	}
	
	public UpdateChecker(Plugin plugin, int projectId) {
		this(plugin, projectId, null);
	}
	
	public UpdateChecker(Plugin plugin, int projectId, String apiKey) {
		PROJECT_ID = projectId;
		PROJECT_SLUG = null;
		API_KEY = apiKey;
		CURRENT_VERSION = plugin.getDescription().getVersion();
		if(plugin.getConfig().getBoolean("update-checker", true)) {
			Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, 0, 6000);
		}
	}
	
	public void run() {
		BufferedReader reader = null;
		try {
			if(PROJECT_ID <= 0) {
				//Find project id
				URL url = new URL(API_HOST + API_QUERY_SEARCH + PROJECT_SLUG);
				URLConnection connection = url.openConnection();
				InputStreamReader isr = new InputStreamReader(connection.getInputStream());
				reader = new BufferedReader(isr);
				
				try {
					JSONArray array = (JSONArray) JSONValue.parse(reader.readLine());
					for(Object obj : array) {
						JSONObject json = (JSONObject) obj;
						int id = (Integer) json.get(API_ID_VALUE);
						String slug = (String) json.get(API_SLUG_VALUE);
						
						if(slug.equals(PROJECT_SLUG)) {
							PROJECT_ID = id;
							break;
						}
					}
				} catch(Exception e) {
					this.error = e.getMessage();
				}
			}
			
			if(PROJECT_ID > 0) {
				//Query file versions
				URL url = new URL(API_HOST + API_QUERY_FILES + PROJECT_ID);
				URLConnection connection = url.openConnection();
				if(API_KEY != null) {
					//Verify account API access
					connection.addRequestProperty("X-API-Key", API_KEY);
				}
				
				//Confirm identity
				connection.addRequestProperty("User-Agent", "UpdateChecker (by lenis0012)");
				
				InputStreamReader isr = new InputStreamReader(connection.getInputStream());
				reader = new BufferedReader(isr);
				JSONArray array = (JSONArray) JSONValue.parse(reader.readLine());
				if(array.size() > 0) {
					JSONObject json = (JSONObject) array.get(array.size() - 1);
					String title = (String) json.get(API_NAME_VALUE);
					String mcVersion = (String) json.get(API_GAME_VERSION_VALUE);
					
					int oldVersion = this.parseInt(CURRENT_VERSION);
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
					if(title.contains(" v")) {
						String[] data = title.split(" v");
						NEW_VERSION = data[data.length - 1];
					} else {
						NEW_VERSION = title;
					}
					
					NEW_BUKKIT_VERSION = mcVersion;
					NEW_MC_VERSION =  mcVersion.replace("CB ", "").split("-")[0];
					this.error = "";
				} else {
					this.error = "No valid files found.";
				}
			} else {
				this.error = "Failed to parse project id, invalid slug?";
			}
		} catch (Exception e) {
			this.error = e.getMessage();
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					;
				}
			}
		}
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
	 * Get current project version
	 * 
	 * @return Current version.
	 */
	public String getCurrentVersion() {
		String title = CURRENT_VERSION;
		if(title.contains(" v")) {
			String[] data = title.split(" v");
			return data[data.length - 1];
		} else {
			return title;
		}
	}
	
	/**
	 * Get latest file version
	 * 
	 * @return Latest file version
	 */
	public String getLatestVersion() {
		return NEW_VERSION;
	}
	
	/**
	 * Get bukkit version from latest file
	 * 
	 * @return Bukkit version from latest file
	 */
	public String getLatestBukkitVersion() {
		return NEW_BUKKIT_VERSION;
	}
	
	/**
	 * Get mc version from latest file
	 * 
	 * @return MC version from latest file
	 */
	public String getLatestMCVersion() {
		return NEW_MC_VERSION;
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