package com.lenis0012.bukkit.marriage2.internal.data;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.misc.LockedReference;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import com.lenis0012.bukkit.marriage2.MData;
import com.lenis0012.bukkit.marriage2.internal.MarriageCore;
import com.lenis0012.bukkit.marriage2.misc.ListQuery;
import org.bukkit.entity.Player;

public class DataManager {
    // Create a data cache to overlap with the pre join event cache
    private final Cache<UUID, MarriageData> marriageDataCache = CacheBuilder.newBuilder().expireAfterWrite(60L, TimeUnit.SECONDS).build();
	private final LockedReference<Connection> supplier;
	private final MarriageCore core;
	private final String prefix;
	
	public DataManager(MarriageCore core, FileConfiguration config) {
		this.core = core;
		Driver driver = Driver.SQLITE;

		String url;
		if(config.getBoolean("MySQL.enabled")) {
			String user = config.getString("MySQL.user", "root");
			String pswd = config.getString("MySQL.password", "");
			String host = config.getString("MySQL.host", "localhost:3306");
			String database = config.getString("MySQL.database", "myserver");
			this.prefix = config.getString("MySQL.prefix", "marriage_");
			url = String.format("jdbc:mysql://%s/%s?user=%s&password=%s", host, database, user, pswd);
			driver = Driver.MYSQL;
		} else {
			url = String.format("jdbc:sqlite:%s", new File(core.getPlugin().getDataFolder(), "database.db").getPath());
			this.prefix = "";
		}
		
		try {
			driver.initiate();
		} catch(Exception e) {
			core.getLogger().log(Level.SEVERE, "Failed to initiate database driver", e);
		}

		// Create cached connection supplier.
		this.supplier = new LockedReference<>(new ConnectionSupplier(url), 30L, TimeUnit.SECONDS, new ConnectionInvalidator());

		DBUpgrade upgrade = new DBUpgrade();
		Connection connection = supplier.access();
		try {
			Statement statement = connection.createStatement();
			driver.runSetup(statement, prefix);
			ResultSet result = statement.executeQuery(String.format("SELECT * FROM %sversion;", prefix));
			if(result.next()) {
				int dbVersion = result.getInt("version_id");
				if(dbVersion < upgrade.getVersionId()) {
					// TODO: Apply database upgrade.
				}
			} else {
				statement.executeUpdate(String.format("INSERT INTO %sversion (version_id) VALUES(%s);", prefix, upgrade.getVersionId()));
			}
		} catch (SQLException e) {
			core.getLogger().log(Level.WARNING, "Failed to initiate database", e);
		} finally {
			supplier.finish();
		}
	}
	
	public MarriagePlayer loadPlayer(UUID uuid) {
		MarriagePlayer player = null;
		Connection connection = supplier.access();
		try {
			PreparedStatement ps = connection.prepareStatement(String.format(
					"SELECT * FROM %splayers WHERE unique_user_id=?;", prefix));
			ps.setString(1, uuid.toString());
			player = new MarriagePlayer(uuid, ps.executeQuery());
			loadMarriages(connection, player, false);
		} catch (SQLException e) {
			core.getLogger().log(Level.WARNING, "Failed to load player data", e);
		} finally {
			supplier.finish();
		}
		
		return player;
	}
	
	public void savePlayer(MarriagePlayer player) {
		Connection connection = supplier.access();
		try {
			PreparedStatement ps = connection.prepareStatement(String.format(
					"SELECT * FROM %splayers WHERE unique_user_id=?;", prefix));
			ps.setString(1, player.getUniqueId().toString());
			ResultSet result = ps.executeQuery();
			if(result.next()) {
				// Already in database (update)
				ps = connection.prepareStatement(String.format(
						"UPDATE %splayers SET gender=?,priest=?,lastlogin=? WHERE unique_user_id=?;", prefix));
				ps.setString(1, player.getGender().toString());
				ps.setBoolean(2, player.isPriest());
				ps.setLong(3, System.currentTimeMillis());
				ps.setString(4, player.getUniqueId().toString());
				ps.executeUpdate();
			} else {
				// Not in database yet
				ps = connection.prepareStatement(String.format(
						"INSERT INTO %splayers (unique_user_id,gender,priest,lastlogin) VALUES(?,?,?,?);", prefix));
				player.save(ps);
				ps.executeUpdate();
			}
		} catch (SQLException e) {
			core.getLogger().log(Level.WARNING, "Failed to save player data", e);
		} finally {
			supplier.finish();
		}
	}

    public void saveMarriage(MarriageData mdata) {
        Connection connection = supplier.access();
        try {
            PreparedStatement ps = connection.prepareStatement(String.format("SELECT * FROM %smarriages WHERE player1=? AND player2=?;", prefix));
            ps.setString(1, mdata.getPlayer1Id().toString());
            ps.setString(2, mdata.getPllayer2Id().toString());
            ResultSet result = ps.executeQuery();
            if(result.next()) {
                // Update existing entry
                ps = connection.prepareStatement(String.format(
                        "UPDATE %smarriages SET player1=?,player2=?,home_world=?,home_x=?,home_y=?,home_z=?,home_yaw=?,home_pitch=?,pvp_enabled=? WHERE id=?;", prefix));
                mdata.save(ps);
                ps.setInt(10, mdata.getId());
                ps.executeUpdate();
            } else {
                ps = connection.prepareStatement(String.format(
                        "INSERT INTO %smarriages (player1,player2,home_world,home_x,home_y,home_z,home_yaw,home_pitch,pvp_enabled) VALUES(?,?,?,?,?,?,?,?,?);", prefix));
                mdata.save(ps);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            core.getLogger().log(Level.WARNING, "Failed to save marriage data", e);
        } finally {
            supplier.finish();
        }
    }
	
	private void loadMarriages(Connection connection, MarriagePlayer player, boolean alt) throws SQLException {
		PreparedStatement ps = connection.prepareStatement(String.format(
				"SELECT * FROM %smarriages WHERE %s=?;", prefix, alt ? "player2" : "player1", prefix));
		ps.setString(1, player.getUniqueId().toString());
		ResultSet result = ps.executeQuery();
		while(result.next()) {
			UUID partnerId = UUID.fromString(result.getString(alt ? "player1" : "player2"));
			Player partner = Bukkit.getPlayer(partnerId);
            MarriageData data;
			if(partner != null && partner.isOnline()) {
				// Copy marriage data from partner to ensure a match.
				data = (MarriageData) core.getMPlayer(partnerId).getMarriage();
			} else if((data = marriageDataCache.getIfPresent(player.getUniqueId())) == null){
                data = new MarriageData(this, result);
                marriageDataCache.put(data.getPlayer1Id(), data);
                marriageDataCache.put(data.getPllayer2Id(), data);
            }

            player.addMarriage(data);
		}
		
		if(!alt) {
			loadMarriages(connection, player, true);
		}
	}

    public void deleteMarriage(UUID player1, UUID player2) {
        Connection connection = supplier.access();
        try {
            PreparedStatement ps = connection.prepareStatement(String.format("DELETE FROM %smarriages WHERE player1=? AND player2=?;", prefix));
            ps.setString(1, player1.toString());
            ps.setString(2, player2.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            core.getLogger().log(Level.WARNING, "Failed to load player data", e);
        } finally {
            supplier.finish();
        }
    }
	
	public ListQuery listMarriages(int scale, int page) {
		Connection connection = supplier.access();
		try {
			// Count rows to get amount of pages
			PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM " + prefix + "marriages;");
			ResultSet result = ps.executeQuery();
			result.next();
			int pages = (int) Math.ceil(result.getInt(1) / (double) scale);
			
			// Fetch te right page
			ps = connection.prepareStatement(String.format(
					"SELECT * FROM %smarriages LIMIT %s OFFSET %s;", prefix, scale, scale * page));
            //"SELECT * FROM %sdata ORDER BY id DESC LIMIT %s OFFSET %s;", prefix, scale, scale * page));
			result = ps.executeQuery();
			
			List<MData> list = Lists.newArrayList();
			while(result.next()) {
				list.add(new MarriageData(this, result));
			}
			
			return new ListQuery(pages, page, list);
		} catch (SQLException e) {
			core.getLogger().log(Level.WARNING, "Failed to load marriage list", e);
			return new ListQuery(0, 0, new ArrayList<MData>());
		} finally {
			supplier.finish();
		}
	}

	private static final class ConnectionSupplier implements Supplier<Connection> {
		private final String url;

		private ConnectionSupplier(String url) {
			this.url = url;
		}

		@Override
		public Connection get() {
			try {
				return DriverManager.getConnection(url);
			} catch(SQLException e) {
				return null;
			}
		}
	}

	private static final class ConnectionInvalidator implements Consumer<Connection> {

		@Override
		public void accept(Connection connection) {
			// Try to close connection
			try {
				connection.close();
			} catch(SQLException e) {
			}
		}
	}
}