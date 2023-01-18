package com.lenis0012.bukkit.marriage2.internal;

import com.lenis0012.bukkit.marriage2.MData;
import com.lenis0012.bukkit.marriage2.MPlayer;
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
import com.lenis0012.pluginutils.modules.configuration.ConfigurationModule;
import com.lenis0012.updater.api.ReleaseType;
import com.lenis0012.updater.api.Updater;
import com.lenis0012.updater.api.UpdaterFactory;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.CustomChart;
import org.bstats.charts.SingleLineChart;
import org.bstats.json.JsonObjectBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

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

    public MarriageCore(MarriagePlugin plugin) {
        super(plugin);
    }

    @Register(name = "config", type = Register.Type.ENABLE, priority = 0)
    public void loadConfig() {
//		plugin.saveDefaultConfig();
        enable();

        // Settings
        ConfigurationModule module = plugin.getModule(ConfigurationModule.class);
        module.registerSettings(Settings.class);
        module.reloadSettings(Settings.class, true);

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
            getLogger().log(Level.INFO, "Attempting to hook using PlotSquared v5 API.");
            Class.forName("com.plotsquared.core.api.PlotAPI");
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
                CommandGender.class,
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
                CommandTeleport.class,
                CommandUpdate.class
        );
    }

    @Register(name = "updater", type = Type.ENABLE, priority = 9)
    public void loadUpdater() {
        UpdaterFactory factory = new UpdaterFactory(plugin, "com.lenis0012.bukkit.marriage2.libs.updater");
        this.updater = factory.newUpdater(plugin.getPluginFile(), Settings.ENABLE_UPDATE_CHECKER.value());
        updater.setChannel(ReleaseType.valueOf(Settings.UPDATER_CHANNEL.value().toUpperCase()));
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

    @Override
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

    @Override
    public MPlayer getMPlayer(Player player) {
        MarriagePlayer mp = players.get(player.getUniqueId());
        if(mp == null) {
            mp = dataManager.loadPlayer(player.getUniqueId());
            players.put(player.getUniqueId(), mp);
        }

        return mp;
    }

    @Override
    public MData marry(MPlayer player1, MPlayer player2) {
        return marry(player1, player2, null);
    }

    @Override
    public MData marry(MPlayer player1, MPlayer player2, MPlayer priest) {
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

    @Override
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

    public void removeMarriage(final MData mdata) {
        new Thread() {
            @Override
            public void run() {
                dataManager.deleteMarriage(mdata.getPlayer1Id(), mdata.getPllayer2Id());
            }
        }.start();
    }

    /**
     * Unload player from the memory
     *
     * @param uuid of player
     */
    public void unloadPlayer(UUID uuid) {
        final MarriagePlayer mPlayer = players.remove(uuid);
        if(mPlayer != null) {
            new Thread() {
                @Override
                public void run() {
                    dataManager.savePlayer(mPlayer);
                }
            }.start();
        }
    }

    public void unloadAll() {
        for(MarriagePlayer mp : players.values()) {
            dataManager.savePlayer(mp);
        }
        players.clear();
    }

    @Override
    public Dependencies dependencies() {
        return dependencies;
    }
}
