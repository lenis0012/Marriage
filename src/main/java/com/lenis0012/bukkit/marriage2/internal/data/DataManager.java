package com.lenis0012.bukkit.marriage2.internal.data;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lenis0012.bukkit.marriage2.MData;
import com.lenis0012.bukkit.marriage2.PlayerGender;
import com.lenis0012.bukkit.marriage2.internal.MarriageCore;
import com.lenis0012.bukkit.marriage2.internal.MarriagePlugin;
import com.lenis0012.bukkit.marriage2.misc.BConfig;
import com.lenis0012.bukkit.marriage2.misc.ListQuery;
import com.lenis0012.pluginutils.sql.DataSourceBuilder;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class DataManager {
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static ExecutorService getExecutorService() {
        return executorService;
    }

    // Create a data cache to overlap with the pre join event cache
    private final Cache<UUID, MarriageData> marriageDataCache = CacheBuilder.newBuilder().expireAfterWrite(60L, TimeUnit.SECONDS).build();
    private final MarriageCore core;
    private DataSource dataSource;
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
        if (dataSource instanceof Closeable) {
            try {
                ((Closeable) dataSource).close();
            } catch (Exception e) {
                core.getLogger().log(Level.SEVERE, "Failed to close data source", e);
            }
        }
    }

    private void loadWithDriver(Driver driver, FileConfiguration config) {
        if(driver == Driver.MYSQL) {
            String host = config.getString("MySQL.host", "localhost:3306");
            this.prefix = config.getString("MySQL.prefix", "marriage_");

            Plugin plugin = JavaPlugin.getPlugin(MarriagePlugin.class);
            this.dataSource = DataSourceBuilder.mysqlBuilder(plugin)
                    .hostname(host.split(":")[0])
                    .port(host.contains(":") ? Integer.parseInt(host.split(":")[1]) : 3306)
                    .database(config.getString("MySQL.database", "myserver"))
                    .username(config.getString("MySQL.user", "root"))
                    .password(config.getString("MySQL.password", ""))
                    .build();
        } else {
            this.dataSource = DataSourceBuilder.sqlite(new File(core.getPlugin().getDataFolder(), "database.db"));
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
                    if(purged > 0) {
                        core.getLogger().log(Level.INFO, "Purged " + purged + " player entries in " + duration + "ms");
                    }
                }
            }, 0L, delayTime);
        }

        DBUpgrade upgrade = new DBUpgrade();
        try(Connection connection = dataSource.getConnection()) {
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
        } catch(SQLException e) {
            core.getLogger().log(Level.WARNING, "Failed to initiate database", e);
        }
    }

    private int purge(long daysInMillis, boolean purgeMarried) {
        String query = String.format("SELECT * FROM %splayers WHERE lastlogin < ?;", prefix);
        try(Connection connection = dataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setLong(1, System.currentTimeMillis() - daysInMillis);
            ResultSet result = ps.executeQuery();
            Set<String> removeList = Sets.newHashSet();
            Set<Integer> removeList2 = Sets.newHashSet();
            while(result.next()) {
                removeList.add(result.getString("unique_user_id"));
            }

            ps.close(); // Release statement
            if(!purgeMarried) {
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
            }

            // Delete player entries
            ps = connection.prepareStatement("DELETE FROM " + prefix + "players WHERE unique_user_id=?;");
            for(String uuid : removeList) {
                ps.setString(1, uuid);
                ps.addBatch();
            }
            ps.executeBatch();
            ps.close();

            // Remove marriage entries
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
        }
    }

    public MarriagePlayer loadPlayer(UUID uuid) {
        MarriagePlayer player = null;
        try(Connection connection = dataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(String.format(
                    "SELECT * FROM %splayers WHERE unique_user_id=?;", prefix));
            ps.setString(1, uuid.toString());
            player = new MarriagePlayer(uuid, ps.executeQuery());
            ps.close(); // Release statement
            loadMarriages(connection, player, false);
        } catch(SQLException e) {
            core.getLogger().log(Level.WARNING, "Failed to load player data", e);
        }

        return player;
    }

    public void savePlayer(MarriagePlayer player) {
        if(player == null || player.getUniqueId() == null) return;
        try(Connection connection = dataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(String.format(
                    "SELECT * FROM %splayers WHERE unique_user_id=?;", prefix));
            ps.setString(1, player.getUniqueId().toString());
            ResultSet result = ps.executeQuery();
            if(result.next()) {
                // Already in database (update)
                PreparedStatement ps2 = connection.prepareStatement(String.format(
                        "UPDATE %splayers SET last_name=?,gender=?,priest=?,lastlogin=? WHERE unique_user_id=?;", prefix));
                ps2.setString(1, player.getLastName());
                ps2.setString(2, player.getChosenGender().map(PlayerGender::getIdentifier).orElse(null));
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
        } catch(SQLException e) {
            core.getLogger().log(Level.WARNING, "Failed to save player data", e);
        }
    }

    public void saveMarriage(MarriageData mdata) {
        try(Connection connection = dataSource.getConnection()) {
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
        } catch(SQLException e) {
            core.getLogger().log(Level.WARNING, "Failed to save marriage data", e);
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
                data = (MarriageData) core.getMPlayer(partner).getMarriage();
            } else if((data = marriageDataCache.getIfPresent(player.getUniqueId())) == null) {
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
        try(Connection connection = dataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(String.format("DELETE FROM %smarriages WHERE player1=? AND player2=?;", prefix));
            ps.setString(1, player1.toString());
            ps.setString(2, player2.toString());
            ps.executeUpdate();
            ps.close();
        } catch(SQLException e) {
            core.getLogger().log(Level.WARNING, "Failed to load player data", e);
        }
    }

    public ListQuery listMarriages(int scale, int page) {
        try(Connection connection = dataSource.getConnection()) {
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
        } catch(SQLException e) {
            core.getLogger().log(Level.WARNING, "Failed to load marriage list", e);
            return new ListQuery(this, 0, 0, new ArrayList<MData>());
        }
    }

    public boolean migrateTo(DataManager db, boolean migrateUnmarriedPlayers) {
        try(Connection connection = dataSource.getConnection()) {
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
        } catch(SQLException e) {
            core.getLogger().log(Level.WARNING, "Failed to load migrate database", e);
        }
        return false;
    }

    public Integer countMarriages() {
        try(Connection connection = dataSource.getConnection()) {
            ResultSet result = connection.createStatement().executeQuery("SELECT COUNT(*) FROM " + prefix + "marriages;");
            return result.next() ? result.getInt(1) : 0;
        } catch(SQLException e) {
            core.getLogger().log(Level.WARNING, "Failed to count marriages", e);
            return null;
        }
    }
}