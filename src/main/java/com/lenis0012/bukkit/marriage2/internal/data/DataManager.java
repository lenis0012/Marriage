package com.lenis0012.bukkit.marriage2.internal.data;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;

import com.lenis0012.bukkit.marriage2.internal.MarriageCore;

public class DataManager {
	private final MarriageCore core;
	private final String url;
	private final String prefix;
	
	public DataManager(MarriageCore core, FileConfiguration config) {
		this.core = core;
		String driver = "org.sqlite.JDBC";
		if(config.getBoolean("MySQL.enabled")) {
			String user = config.getString("MySQL.user", "root");
			String pswd = config.getString("MySQL.password", "");
			String host = config.getString("MySQL.host", "localhost:3306");
			String database = config.getString("MySQL.database", "myserver");
			this.prefix = config.getString("MySQL.prefix", "marriage_");
			this.url = String.format("jdbc:mysql://%s/%s?user=%s&password=%s", host, database, user, pswd);
		} else {
			this.url = String.format("jdbc:sqlite:%s", new File(core.getPlugin().getDataFolder(), "database.db"));
			this.prefix = "";
		}
		
		try {
			Class.forName(driver);
		} catch(Exception e) {
			core.getLogger().log(Level.SEVERE, "Failed to initiate database driver", e);
		}
		
		Connection connection = newConnection();
		try {
			Statement statement = connection.createStatement();
			statement.executeUpdate(String.format("CREATE TABLE IF NOT EXISTS %splayers ("
					+ "id NOT NULL UNIQUE AUTO_INCREMENT,"
					+ "unique_user_id VARCHAR(128) NOT NULL UNIQUE,"
					+ "gender VARCHAR(32),"
					+ "lastlogin BIGINT,"
					+ "PRIMARY KEY (id);", prefix));
			statement.executeUpdate(String.format("CREATE TABLE IF NOT EXISTS %sdata ("
					+ "id NOT NULL UNIQUE AUTO_INCREMENT,"
					+ "player1 VARCHAR(128) NOT NULL,"
					+ "player2 VARCHAR(128) NOT NULL,"
					+ "home_world VARCHAR(128) NOT NULL,"
					+ "home_x DOUBLE,"
					+ "home_y DOUBLE,"
					+ "home_z DOUBLE,"
					+ "home_yaw FLOAT,"
					+ "home_pitch FLOAT,"
					+ "pvp_enabled BIT,"
					+ "PRIMARY KEY (id);", prefix));
		} catch (SQLException e) {
			core.getLogger().log(Level.WARNING, "Failed to load player data", e);
		} finally {
			if(connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					;
				}
			}
		}
	}
	
	public MarriagePlayer loadPlayer(UUID uuid) {
		MarriagePlayer player = null;
		Connection connection = newConnection();
		try {
			PreparedStatement ps = connection.prepareStatement(String.format(
					"SELECT * FROM %splayers WHERE unique_user_id=?;", prefix));
			ps.setString(1, uuid.toString());
			player = new MarriagePlayer(uuid, ps.executeQuery());
			loadMarriages(connection, player, false);
		} catch (SQLException e) {
			core.getLogger().log(Level.WARNING, "Failed to load player data", e);
		} finally {
			if(connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					;
				}
			}
		}
		
		return player;
	}
	
	private void loadMarriages(Connection connection, MarriagePlayer player, boolean alt) throws SQLException {
		PreparedStatement ps = connection.prepareStatement(String.format(
				"SELECT * FROM %sdata WHERE %s=?;", alt ? "player2" : "player1", prefix));
		ResultSet result = ps.executeQuery();
		while(result.next()) {
			player.addMarriage(new MarriageData(result));
		}
		
		if(!alt) {
			loadMarriages(connection, player, true);
		}
	}
	
	private Connection newConnection() {
		try {
			return DriverManager.getConnection(url);
		} catch (SQLException e) {
			core.getLogger().log(Level.WARNING, "Failed to connect to database", e);
			return null;
		}
	}
}