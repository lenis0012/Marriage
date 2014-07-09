package com.lenis0012.bukkit.marriage.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.lenis0012.bukkit.marriage.Marriage;

public class UUIDConverter {
	private final Marriage plugin;
	private final Logger logger;
	
	public UUIDConverter(Marriage plugin) {
		this.plugin = plugin;
		this.logger = plugin.getLogger();
		logger.info("Starting to convert all data to UUID's");
		this.startConversion();
	}
	
	public void startConversion() {
		File file = new File(plugin.getDataFolder(), "playerdata");
		File backupFile = new File(plugin.getDataFolder(), "playerdata-backup");
		File tempFile = new File(plugin.getDataFolder(), "playerdata-temp");
		List<Conversion> conversions = new ArrayList<Conversion>();
		
		//Remove old directories if exists
		if(backupFile.exists()) {
			logger.info("Old backup found and will be deleted.");
			deleteDirectory(backupFile);
		} if(tempFile.exists()) {
			logger.info("Old temp file found and will be deleted.");
			deleteDirectory(tempFile);
		}
		
		//Create temp file.
		tempFile.mkdirs();
		
		//Loop throguh all current data
		for(File dfile : file.listFiles()) {
			//Check if data file is yml.
			if(dfile.getName().endsWith(".yml")) {
				//Parse player name
				String playerName = dfile.getName().replace(".yml", "");
				
				//Create conversion instance
				Conversion conversion = new Conversion(playerName, dfile);
				
				//Add conversion to list.
				conversions.add(conversion);
			}
		}
		
		logger.info("Found " + conversions.size() + " player files, converting files.");
		logger.info("This part may take some time, go get a coffee :)");
		
		//Calculate estemated time
		long startTime = System.currentTimeMillis();
		getUUIDByUsername("lenis0012");
		long duration = System.currentTimeMillis() - startTime;
		duration = (duration * conversions.size()) / 1000;
		logger.info("Estemated duration: " + duration + " seconds.");
		
		//Start conversion
		for(Conversion conversion : conversions) {
			conversion.convert(tempFile);
		}
		
		logger.info("Successfully converted all player data.");
		logger.info("Renaming directories...");
		file.renameTo(backupFile);
		tempFile.renameTo(file);
		
		try { new File(plugin.getDataFolder(), ".update-lock").createNewFile(); } catch(Exception e1) { ; }
		logger.info("Conversion completed.");
	}
	
	public static final class Conversion {
		private final String playerName;
		private final File oldFile;
		
		public Conversion(String playerName, File oldFile) {
			this.playerName = playerName;
			this.oldFile = oldFile;
		}
		
		public void convert(File directory) {
			String uuid = getUUIDByUsername(playerName);
			File newFile = new File(directory, uuid + ".yml");
			
			//Try to copy file
			try {
				newFile.createNewFile();
				FileInputStream input = new FileInputStream(oldFile);
				FileOutputStream output = new FileOutputStream(newFile);
				byte[] buffer = new byte[1024];
				int length;
				while((length = input.read(buffer)) != -1) {
					output.write(buffer, 0, length);
				}
				
				output.close();
				input.close();
			} catch(Exception e) {
				;
			}
		}
	}
	
	private static final void deleteDirectory(File directory) {
		//Loop through all dir conents
		for(File file : directory.listFiles()) {
			if(file.isDirectory()) {
				//If file content is directory, remove that first.
				deleteDirectory(file);
			} else {
				//Else just try delete file
				try {
					file.delete();
				} catch(Exception e) {
					;
				}
			}
		}
		
		//When directory is empty, try remove it
		try {
			directory.delete();
		} catch(Exception e) {
			;
		}
	}
	
	private static final String getUUIDByUsername(String input) {
		String uuid = null;

		try {
			HttpsURLConnection con = (HttpsURLConnection) new URL("https://api.mojang.com/profiles/page/1").openConnection();

			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setDoOutput(true);

			String toPost = "{\"name\":\"" + input + "\",\"agent\":\"minecraft\"}";
			DataOutputStream dos = new DataOutputStream(con.getOutputStream());

			dos.writeBytes(toPost);
			dos.close();

			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String json = br.readLine();

			br.close();
			// Parse it
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(json);
			uuid = (String) ((JSONObject) ((JSONArray) ((JSONObject) obj).get("profiles")).get(0)).get("id");
			
			//Split uuid in to 5 components
			String[] uuidComponents = new String[] {
					uuid.substring(0, 8),
					uuid.substring(8, 12),
					uuid.substring(12, 16),
					uuid.substring(16, 20),
					uuid.substring(20, uuid.length())
			};
			
			//Combine components with a dash
			StringBuilder builder = new StringBuilder();
			for(String component : uuidComponents) {
				builder.append(component).append('-');
			}
			
			//Correct uuid length, remove last dash
			builder.setLength(builder.length() - 1);
			uuid = builder.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			uuid = "error-" + ex.getMessage();
		}
		
		return uuid;
	}
}