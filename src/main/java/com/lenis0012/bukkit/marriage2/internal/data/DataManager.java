package com.lenis0012.bukkit.marriage2.internal.data;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lenis0012.bukkit.marriage2.internal.MarriagePlugin;
import com.lenis0012.bukkit.marriage2.misc.BConfig;
import com.lenis0012.bukkit.marriage2.misc.LockedReference;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import com.lenis0012.bukkit.marriage2.MData;
import com.lenis0012.bukkit.marriage2.internal.MarriageCore;
import com.lenis0012.bukkit.marriage2.misc.ListQuery;
import org.bukkit.entity.Player;

public class DataManager {
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static ExecutorService getExecutorService() {
        return executorService;
    }

    // Create a data cache to overlap with the pre join event cache
    private final Cache<UUID, MarriageData> marriageDataCache = CacheBuilder.newBuilder().expireAfterWrite(60L, TimeUnit.SECONDS).build();
    private final MarriageCore core;
	private LockedReference<Connection> supplier;
	private String prefix;

    public DataManager(MarriageCore core) {
        this.core = core;
        File configFile = new File(core.getPlugin().getDataFolder(), "database-settings.yml");
        if(!configFile.exists()) {
            BConfig.copyFile(core.getPlugin().getResource("database-settings.yml"), configFile);
        }

        FileConfiguration config = core.getBukkitConfig("database-settings.yml");
        Driver driver = config.getBoolean("MySQL.enabled") ? Driver.MYSQL : Driver.SQLITE;
        loadWithDriver(driver, config);
    }
	
	public DataManager(MarriageCore core, Driver driver) {
		this.core = core;
        FileConfiguration config = core.getBukkitConfig("database-settings.yml");
        loadWithDriver(driver, config);
	}

    public void close() {
        supplier.invalidateNow();
    }

    private void loadWithDriver(Driver driver, FileConfiguration config) {
        String url;
        if(driver == Driver.MYSQL) {
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

        // Purge system
        if(config.getBoolean("auto-purge.enabled")) {
            final long delayTime = 20L * 60 * 60; // Every hour
            final int days = config.getInt("auto-purge.purge-after-days", 45);
            final boolean purgeMarried = config.getBoolean("auto-purge.purge-married-players", false);
            final long daysInMillis = days * 24 * 60 * 60 * 1000L;
            Bukkit.getScheduler().runTaskTimerAsynchronously(MarriagePlugin.getCore().getPlugin(), new Runnable() {
                @Override
                public void run() {
                    long startTime = System.currentTimeMillis();
                    int purged = purge(daysInMillis, purgeMarried);
                    long duration = System.currentTimeMillis() - startTime;
                    core.getLogger().log(Level.INFO, "Purged " + purged + " player entried in " + duration + "ms");
                }
            }, 0L, delayTime);
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
                if(dbVersion >= 2) {
                    // Fix for people that first installed on v2
                    DatabaseMetaData metadata = connection.getMetaData();
                    ResultSet res = metadata.getColumns(null, null, prefix + "players", "last_name");
                    if(!res.next()) {
                        statement.execute("ALTER TABLE " + prefix + "players ADD last_name VARCHAR(16);");
                    }
                }

                if(dbVersion < upgrade.getVersionId()) {
                    upgrade.run(statement, dbVersion, prefix);
                    PreparedStatement ps = connection.prepareStatement("UPDATE " + prefix + "version SET version_id=? WHERE version_id=?;");
                    ps.setInt(1, upgrade.getVersionId());
                    ps.setInt(2, dbVersion);
                    ps.executeUpdate();
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

    private int purge(long daysInMillis, boolean purgeMarried) {
        String query = String.format("SELECT * FROM %splayers WHERE lastlogin < ?;", prefix);
        Connection connection = supplier.access();
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setLong(1, System.currentTimeMillis() - daysInMillis);
            ResultSet result = ps.executeQuery();
            Set<String> removeList = Sets.newHashSet();
            Set<Integer> removeList2 = Sets.newHashSet();
            while(result.next()) {
                removeList.add(result.getString("unique_user_id"));
            }

            ps.close(); // Release statement
            supplier.finish(); // Let queued actions run first
            if(!purgeMarried) {
                connection = supplier.access();
                ps = connection.prepareStatement("SELECT * FROM " + prefix + "marriages;");
                result = ps.executeQuery();
                while(result.next()) {
                    boolean remove = removeList.remove(result.getString("player1"));
                    remove = remove || removeList.remove(result.getString("player2"));
                    if(remove) {
                        removeList2.add(result.getInt("id"));
                    }
                }
                ps.close(); // Release statement
                supplier.finish(); // Let queued actions run first
            }

            // Delete player entries
            connection = supplier.access();
            ps = connection.prepareStatement("DELETE FROM " + prefix + "players WHERE unique_user_id=?;");
            for(String uuid : removeList) {
                ps.setString(1, uuid);
                ps.addBatch();
            }
            ps.executeBatch();
            ps.close();
            supplier.finish();

            // Remove marriage entries
            connection = supplier.access();
            ps = connection.prepareStatement("DELETE FROM " + prefix + "marriages WHERE id=?;");
            for(int id : removeList2) {
                ps.setInt(1, id);
                ps.addBatch();
            }
            ps.executeBatch();
            ps.close();
            return removeList.size();
        } catch(SQLException e) {
            core.getLogger().log(Level.WARNING, "Failed to purge user data", e);
            return 0;
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
            ps.close(); // Release statement
			loadMarriages(connection, player, false);
		} catch (SQLException e) {
			core.getLogger().log(Level.WARNING, "Failed to load player data", e);
		} finally {
			supplier.finish();
		}
		
		return player;
	}
	
	public void savePlayer(MarriagePlayer player) {
        if(player == null || player.getUniqueId() == null) return;
		Connection connection = supplier.access();
		try {
			PreparedStatement ps = connection.prepareStatement(String.format(
					"SELECT * FROM %splayers WHERE unique_user_id=?;", prefix));
			ps.setString(1, player.getUniqueId().toString());
			ResultSet result = ps.executeQuery();
			if(result.next()) {
				// Already in database (update)
				PreparedStatement ps2 = connection.prepareStatement(String.format(
						"UPDATE %splayers SET last_name=?,gender=?,priest=?,lastlogin=? WHERE unique_user_id=?;", prefix));
                ps2.setString(1, player.getLastName());
				ps2.setString(2, player.getGender().toString());
				ps2.setBoolean(3, player.isPriest());
				ps2.setLong(4, System.currentTimeMillis());
				ps2.setString(5, player.getUniqueId().toString());
				ps2.executeUpdate();
                ps2.close();
			} else {
				// Not in database yet
				PreparedStatement ps2 = connection.prepareStatement(String.format(
						"INSERT INTO %splayers (unique_user_id,last_name,gender,priest,lastlogin) VALUES(?,?,?,?,?);", prefix));
				player.save(ps2);
				ps2.executeUpdate();
                ps2.close();
			}
            ps.close();
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
                PreparedStatement ps2 = connection.prepareStatement(String.format(
                        "UPDATE %smarriages SET player1=?,player2=?,home_world=?,home_x=?,home_y=?,home_z=?,home_yaw=?,home_pitch=?,pvp_enabled=? WHERE id=?;", prefix));
                mdata.save(ps2);
                ps2.setInt(10, mdata.getId());
                ps2.executeUpdate();
                ps2.close();
            } else {
                PreparedStatement ps2 = connection.prepareStatement(String.format(
                        "INSERT INTO %smarriages (player1,player2,home_world,home_x,home_y,home_z,home_yaw,home_pitch,pvp_enabled) VALUES(?,?,?,?,?,?,?,?,?);", prefix));
                mdata.save(ps2);
                ps2.executeUpdate();
                ps2.close();
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
			if(partner != null && partner.isOnline() && core.isMPlayerSet(partner.getUniqueId())) {
				// Copy marriage data from partner to ensure a match.
				data = (MarriageData) core.getMPlayer(partnerId).getMarriage();
			} else if((data = marriageDataCache.getIfPresent(player.getUniqueId())) == null){
                data = new MarriageData(this, result);
                marriageDataCache.put(data.getPlayer1Id(), data);
                marriageDataCache.put(data.getPllayer2Id(), data);
            }

            player.addMarriage(data);
		}
        ps.close();
		
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
            ps.close();
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
            ps.close();
			
			return new ListQuery(this, pages, page, list);
		} catch (SQLException e) {
			core.getLogger().log(Level.WARNING, "Failed to load marriage list", e);
			return new ListQuery(this, 0, 0, new ArrayList<MData>());
		} finally {
			supplier.finish();
		}
	}

    public boolean migrateTo(DataManager db, boolean migrateUnmarriedPlayers) {
        Connection connection = supplier.access();
        try {
            // Migrate players
            core.getLogger().log(Level.INFO, "Migrating player data... (may take A WHILE)");
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + prefix + "players;");
            ResultSet result = ps.executeQuery();
            while(result.next()) {
                UUID uuid = UUID.fromString(result.getString("unique_user_id"));
                MarriagePlayer player = new MarriagePlayer(uuid, result);
                if(!player.isMarried() && !migrateUnmarriedPlayers) continue;
                db.savePlayer(player);
            }
            ps.close();

            core.getLogger().log(Level.INFO, "Migrating marriage data...");
            ps = connection.prepareStatement("SELECT * FROM " + prefix + "marriages;");
            result = ps.executeQuery();
            while(result.next()) {
                MarriageData data = new MarriageData(this, result);
                db.saveMarriage(data);
            }
            ps.close();

            core.getLogger().log(Level.INFO, "Migration complete!");
            return true;
        } catch (SQLException e) {
            core.getLogger().log(Level.WARNING, "Failed to load migrate database", e);
        } finally {
            supplier.finish();
        }
        return false;
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