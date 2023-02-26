package com.lenis0012.bukkit.marriage2.internal;

import com.lenis0012.bukkit.marriage2.Genders;
import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.Relationship;
import com.lenis0012.bukkit.marriage2.Relationship;
import com.lenis0012.bukkit.marriage2.commands.*;
import com.lenis0012.bukkit.marriage2.config.Message;
import com.lenis0012.bukkit.marriage2.config.Permissions;
import com.lenis0012.bukkit.marriage2.config.Settings;
import com.lenis0012.bukkit.marriage2.events.PlayerMarryEvent;
import com.lenis0012.bukkit.marriage2.internal.Register.Type;
import com.lenis0012.bukkit.marriage2.internal.data.DataConverter;
import com.lenis0012.bukkit.marriage2.internal.data.DataManager;
import com.lenis0012.bukkit.marriage2.internal.data.MarriageData;
import com.lenis0012.bukkit.marriage2.internal.data.MarriagePlayer;
import com.lenis0012.bukkit.marriage2.listeners.*;
import com.lenis0012.bukkit.marriage2.misc.ListQuery;
import com.lenis0012.pluginutils.config.AutoSavePolicy;
import com.lenis0012.pluginutils.config.CommentConfiguration;
import com.lenis0012.pluginutils.config.mapping.InternalMapper;
import com.lenis0012.pluginutils.updater.Updater;
import com.lenis0012.pluginutils.updater.UpdaterFactory;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class MarriageCore extends MarriageBase {
    private final Map<UUID, MarriagePlayer> players = Collections.synchronizedMap(new HashMap<UUID, MarriagePlayer>());
    private DataManager dataManager;
    private Updater updater;
    private Dependencies dependencies;
    private InternalMapper internalMapper;
    private ClassLoader classLoader;

    public MarriageCore(MarriagePlugin plugin, ClassLoader classLoader) {
        super(plugin);
        this.classLoader = classLoader;
    }

    @Register(name = "config", type = Register.Type.ENABLE, priority = 0)
    public void loadConfig() {
        enable();

        // Settings
        this.internalMapper = new InternalMapper();
        CommentConfiguration configuration = new CommentConfiguration(new File(plugin.getDataFolder(), "config.yml"));
        internalMapper.registerSettingsClass(Settings.class, configuration, AutoSavePolicy.ON_CHANGE);
        migrateSettings();
        reloadSettings();

        // Messages
        Message.reloadAll(this);

        // Permissions
        if(Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            if(!Permissions.setupPermissions()) {
                getLogger().log(Level.WARNING, "Vault was found, but no permission provider was detected!");
                getLogger().log(Level.INFO, "Falling back to bukkit permissions.");
            }
        } else {
            getLogger().log(Level.INFO, "Vault was not found, if you are having permission issues, please install it!");
            getLogger().log(Level.INFO, "Falling back to bukkit permissions.");
        }
    }

    public void migrateSettings() {
        FileConfiguration configuration = plugin.getConfig();
        if(configuration.isSet("chat.male-prefix")) {
            configuration.set("genders.male.chat-prefix", configuration.getString("chat.male-prefix"));
            configuration.set("genders.female.chat-prefix", configuration.getString("chat.female-prefix"));
            configuration.set("chat.male-prefix", null);
            configuration.set("chat.female-prefix", null);
            plugin.saveConfig();
        }
    }

    public void reloadSettings() {
        Message.reloadAll(this);
        for(String identifier : Settings.GENDER_OPTIONS.value().getKeys(false)) {
            Genders.removeGenderOption(identifier);
        }
        internalMapper.loadSettings(Settings.class, true);
        for(Map.Entry<String, Object> entry : Settings.GENDER_OPTIONS.value().getValues(false).entrySet()) {
            String identifier = entry.getKey();
            ConfigurationSection map = (ConfigurationSection) entry.getValue();
            String displayName = map.get("display-name", "").toString();
            String chatPrefix = map.get("chat-prefix", "").toString();
            MarriageGender gender = new MarriageGender(identifier, displayName, chatPrefix);
            Genders.addGenderOption(gender);
        }
    }

    @Register(name = "metrics", type = Register.Type.ENABLE, priority = 1)
    public void loadMetrics() {
        final int pluginId = 17462;
        Metrics metrics = new Metrics(plugin, pluginId);
        metrics.addCustomChart(new SingleLineChart("marriages", () -> dataManager.countMarriages()));
    }

    @Register(name = "dependencies", type = Type.ENABLE, priority = 1)
    public void loadDependencies() {
        this.dependencies = new Dependencies(this);
        if(Settings.PLOTSQUARED_AUTO_TRUST.value() && Bukkit.getPluginManager().isPluginEnabled("PlotSquared")) {
            Plugin plotSquared = Bukkit.getPluginManager().getPlugin("PlotSquared");
            getLogger().log(Level.INFO, "Detected PlotSquared v" + plotSquared.getDescription().getVersion() + ". Attempting to hook.");
            hookPlotSquared();
        }
    }

    @Register(name = "database", type = Register.Type.ENABLE)
    public void loadDatabase() {
        this.dataManager = new DataManager(this);

        // Load all players
        for(Player player : Bukkit.getOnlinePlayers()) {
            MarriagePlayer mp = dataManager.loadPlayer(player.getUniqueId());
            setMPlayer(player.getUniqueId(), mp);
        }
    }

    @Register(name = "listeners", type = Register.Type.ENABLE)
    public void registerListeners() {
        register(new PlayerListener(this));
        register(new ChatListener(this));
        register(new DatabaseListener(this));
        register(new KissListener(this));
        register(new UpdateListener(this));
    }

    private void hookPlotSquared() {
        try {
            getLogger().log(Level.INFO, "Attempting to hook using PlotSquared v6 API.");
            Class.forName("com.plotsquared.core.PlotAPI");
            register(new V6PlotSquaredListener());
            getLogger().log(Level.INFO, "Success! Auto-trust has been enabled.");
            return;
        } catch (Exception e) {
        }

        try {
            getLogger().log(Level.INFO, "Attempting to hook using PlotSquared legacy API.");
            Class.forName("com.intellectualcrafters.plot.PS");
            register(new LegacyPlotSquaredListener());
            getLogger().log(Level.INFO, "Success! Auto-trust has been enabled.");
            return;
        } catch (Exception e) {
        }

        getLogger().log(Level.WARNING, "Failed to hook with PlotSquared, please use v5 for full support.");
    }

    @Register(name = "commands", type = Register.Type.ENABLE)
    public void registerCommands() {
        register(
                CommandChat.class,
                CommandChatSpy.class,
                CommandDivorce.class,
                CommandGift.class,
                CommandHeal.class,
                CommandHelp.class,
                CommandHome.class,
                CommandList.class,
                CommandMarry.class,
                CommandMigrate.class,
                CommandPriest.class,
                CommandPVP.class,
                CommandReload.class,
                CommandSeen.class,
                CommandSethome.class,
                CommandTeleport.class
        );
        if (Settings.GENDERS_ENABLED.value()) {
            register(CommandGender.class);
        }
    }

    @Register(name = "updater", type = Type.ENABLE, priority = 9)
    public void loadUpdater() {
        if(!Settings.ENABLE_UPDATE_CHECKER.value()) {
            this.updater = null;
            return;
        }
        try {
            this.updater = UpdaterFactory.provideBest(plugin, classLoader)
                .getUpdater(plugin);
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "Failed to load update checker", e);
        }
    }

    @Register(name = "converter", type = Register.Type.ENABLE, priority = 10)
    public void loadConverter() {
        DataConverter converter = new DataConverter(this);
        if(converter.isOutdated()) {
            converter.convert();
        }
    }

    @Register(name = "database", type = Register.Type.DISABLE)
    public void saveDatabase() {
        unloadAll();
        dataManager.close();
    }

    public MPlayer getMPlayer(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if(player != null && player.isOnline()) {
            return getMPlayer(player);
        }

        MarriagePlayer mp = players.get(uuid);
        if(mp == null) {
            // Load from database, but don't save.
            mp = dataManager.loadPlayer(uuid);
        }

        return mp;
    }

    public MPlayer getMPlayer(Player player) {
        MarriagePlayer mp = players.get(player.getUniqueId());
        if(mp == null) {
            mp = dataManager.loadPlayer(player.getUniqueId());
            players.put(player.getUniqueId(), mp);
        }

        return mp;
    }

    public Relationship marry(MPlayer player1, MPlayer player2) {
        return marry(player1, player2, null);
    }

    public Relationship marry(MPlayer player1, MPlayer player2, MPlayer priest) {
        PlayerMarryEvent event = new PlayerMarryEvent(player1, player2, priest);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) {
            return null;
        }

        MarriageData mdata = new MarriageData(dataManager, player1.getUniqueId(), player2.getUniqueId());
        mdata.saveAsync();
        ((MarriagePlayer) player1).addMarriage(mdata);
        ((MarriagePlayer) player2).addMarriage(mdata);
        return mdata;
    }

    public ListQuery getMarriageList(int scale, int page) {
        return dataManager.listMarriages(scale, page);
    }

    public void setMPlayer(UUID uuid, MarriagePlayer mp) {
        players.put(uuid, mp);
    }

    public boolean isMPlayerSet(UUID uuid) {
        return players.containsKey(uuid);
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public Updater getUpdater() {
        return updater;
    }

    public void removeMarriage(final Relationship mdata) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            dataManager.deleteMarriage(mdata.getPlayer1Id(), mdata.getPllayer2Id());
        });
    }

    /**
     * Unload player from the memory
     *
     * @param uuid of player
     */
    public void unloadPlayer(UUID uuid) {
        final MarriagePlayer mPlayer = players.remove(uuid);
        if(mPlayer != null) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                dataManager.savePlayer(mPlayer);
            });
        }
    }

    public void savePlayer(final MarriagePlayer mPlayer) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            dataManager.savePlayer(mPlayer);
        });
    }

    public void unloadAll() {
        for(MarriagePlayer mp : players.values()) {
            dataManager.savePlayer(mp);
        }
        players.clear();
    }

    public Dependencies dependencies() {
        return dependencies;
    }
}
