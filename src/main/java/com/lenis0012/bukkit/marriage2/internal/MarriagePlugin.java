package com.lenis0012.bukkit.marriage2.internal;

import com.lenis0012.bukkit.marriage2.Genders;
import com.lenis0012.bukkit.marriage2.commands.*;
import com.lenis0012.bukkit.marriage2.config.Message;
import com.lenis0012.bukkit.marriage2.config.Permissions;
import com.lenis0012.bukkit.marriage2.config.Settings;
import com.lenis0012.bukkit.marriage2.internal.data.DataConverter;
import com.lenis0012.bukkit.marriage2.internal.data.DataManager;
import com.lenis0012.bukkit.marriage2.internal.data.MarriagePlayer;
import com.lenis0012.bukkit.marriage2.listeners.*;
import com.lenis0012.bukkit.marriage2.misc.BConfig;
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
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Map;
import java.util.logging.Level;

public class MarriagePlugin extends JavaPlugin {
    private static MarriageCore core;

    public static MarriageCore getCore() {
        return core;
    }

    public static Dependencies dependencies() {
        return JavaPlugin.getPlugin(MarriagePlugin.class).dependencies;
    }

    private DataManager dataManager;
    private Updater updater;
    private Dependencies dependencies;
    private InternalMapper internalMapper;
    private MarriageCommandExecutor commandExecutor;

    protected File getPluginFile() {
        return getFile();
    }

    @Override
    public void onLoad() {
        MarriagePlugin.core = new MarriageCore(this);
    }

    @Override
    public void onEnable() {
        this.commandExecutor = new MarriageCommandExecutor(core);
        getCommand("marry").setExecutor(commandExecutor);

        loadConfig();
        loadDatabase();
        loadMetrics();
        loadDependencies();
        registerListeners();
        registerCommands();
        loadUpdater();
        loadConverter();
    }

    @Override
    public void onDisable() {
        saveDatabase();
    }

    @Register(name = "config", type = Register.Type.ENABLE, priority = 0)
    public void loadConfig() {
        // Settings
        this.internalMapper = new InternalMapper();
        CommentConfiguration configuration = new CommentConfiguration(new File(getDataFolder(), "config.yml"));
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
        FileConfiguration configuration = getConfig();
        if(configuration.isSet("chat.male-prefix")) {
            configuration.set("genders.male.chat-prefix", configuration.getString("chat.male-prefix"));
            configuration.set("genders.female.chat-prefix", configuration.getString("chat.female-prefix"));
            configuration.set("chat.male-prefix", null);
            configuration.set("chat.female-prefix", null);
            saveConfig();
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
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new SingleLineChart("marriages", () -> dataManager.countMarriages()));
    }

    @Register(name = "dependencies", type = Register.Type.ENABLE, priority = 1)
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
            core.setMPlayer(player.getUniqueId(), mp);
        }
    }

    @Register(name = "listeners", type = Register.Type.ENABLE)
    public void registerListeners() {
        register(new PlayerListener(core));
        register(new ChatListener(core));
        register(new DatabaseListener(this, core));
        register(new KissListener(core));
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
            // ignore
        }

        try {
            getLogger().log(Level.INFO, "Attempting to hook using PlotSquared legacy API.");
            Class.forName("com.intellectualcrafters.plot.PS");
            register(new LegacyPlotSquaredListener());
            getLogger().log(Level.INFO, "Success! Auto-trust has been enabled.");
            return;
        } catch (Exception e) {
            // ignore
        }

        getLogger().log(Level.WARNING, "Failed to hook with PlotSquared, please use v5 for full support.");
    }

    @Register(name = "commands", type = Register.Type.ENABLE)
    public void registerCommands() {
        registerCommands(
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
            registerCommands(CommandGender.class);
        }
    }

    @Register(name = "updater", type = Register.Type.ENABLE, priority = 9)
    public void loadUpdater() {
        if(!Settings.ENABLE_UPDATE_CHECKER.value()) {
            this.updater = null;
            return;
        }
        try {
            this.updater = UpdaterFactory.provideBest(this, getClassLoader())
                .getUpdater(this);
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "Failed to load update checker", e);
        }
    }

    @Register(name = "converter", type = Register.Type.ENABLE, priority = 10)
    public void loadConverter() {
        DataConverter converter = new DataConverter(this, core);
        if(converter.isOutdated()) {
            converter.convert();
        }
    }

    @Register(name = "database", type = Register.Type.DISABLE)
    public void saveDatabase() {
        core.unloadAll();
        dataManager.close();
    }

    public void register(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

    public void registerCommands(Class<? extends Command>... commandClasses) {
        for(Class<? extends Command> cmdclass : commandClasses) {
            commandExecutor.register(cmdclass);
        }
    }

    public BConfig getBukkitConfig(String fileName) {
        File file = new File(getDataFolder(), fileName);
        return new BConfig(this, file);
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public MarriageCommandExecutor getCommandExecutor() {
        return commandExecutor;
    }

    public Updater getUpdater() {
        return updater;
    }
}