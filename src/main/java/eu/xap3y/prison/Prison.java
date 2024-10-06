package eu.xap3y.prison;

import com.github.fierioziy.particlenativeapi.api.ParticleNativeAPI;
import com.github.fierioziy.particlenativeapi.core.ParticleNativeCore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.xap3y.prison.api.enchants.ExplosiveEnchant;
import eu.xap3y.prison.api.enchants.TestEnchant;
import eu.xap3y.prison.api.gui.StaticItems;
import eu.xap3y.prison.commands.RootCommand;
import eu.xap3y.prison.listeners.*;
import eu.xap3y.prison.manager.CommandManager;
import eu.xap3y.prison.manager.ConfigManager;
import eu.xap3y.prison.manager.EnchantManager;
import eu.xap3y.prison.services.BoardService;
import eu.xap3y.prison.services.CellService;
import eu.xap3y.prison.storage.ConfigDb;
import eu.xap3y.prison.storage.PlayerStorage;
import eu.xap3y.prison.storage.StorageManager;
import eu.xap3y.prison.util.LogLogger;
import eu.xap3y.xagui.XaGui;
import eu.xap3y.xalib.managers.Texter;
import eu.xap3y.xalib.objects.TexterObj;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Prison extends JavaPlugin {

    public static Prison INSTANCE;

    public static Texter texter;

    public static XaGui xagui;

    public static Gson gson;

    public static final String ver = "@VERSION@";
    public static final String main = "@MAIN@";

    public static final boolean DEBUG = false;

    public static ParticleNativeAPI parApi;

    @Override
    public void onEnable() {
        INSTANCE = this;
        parApi = ParticleNativeCore.loadAPI(this);
        //  Initializing XaGUI  \\
        xagui = new XaGui(this);

        //  Creating parser & Parsing command classes below  \\
        CommandManager cmdManager = new CommandManager();
        cmdManager.parse(new RootCommand());

        //  Saving if not exists & Reloading config file  \\
        ConfigManager.reloadConfig();

        // Load scoreboard configuration
        BoardService.loadConfig();

        //  Setting up texter  \\
        String prefix = getConfig().getString("prefix");
        if (prefix == null) prefix = "&7[&bPrison&7] &r";
        texter = new Texter(new TexterObj(prefix, false, null));

        //   Registering listeners  \\

        /*PluginManager manager = getServer().getPluginManager();
        registerListeners(manager);*/


        //  Registering PlaceholderAPI  \\
        //registerPapi();

        gson = new GsonBuilder().setPrettyPrinting().create();

        StorageManager.loadBlocks();

        /*for (Map.Entry<Material, Block> entry : StorageManager.getBlocks().entrySet()) {
            texter.console("&a" + entry.getKey().name() + " &7-Percentage &a" + entry.getValue().getPercentage() + " &7-XP &a" + entry.getValue().getXp() + " &7-COINS &a" + entry.getValue().getCoins());
        }*/

        PlayerStorage.loadPlayersFromStorage();

        registerListeners(getServer().getPluginManager());

        StaticItems.init();

        CellService.loadCellsLocs();

        if (DEBUG) {
            org.apache.logging.log4j.Logger rootLogger = org.apache.logging.log4j.LogManager.getRootLogger();
            org.apache.logging.log4j.core.Logger logger = (org.apache.logging.log4j.core.Logger) rootLogger;
            logger.addFilter(new LogLogger());
        }

        registerPapi();

        EnchantManager.registerEnchant("test", new TestEnchant());
        EnchantManager.registerEnchant("tnt", new ExplosiveEnchant());

        reload();

        // Note for dev: Call reload() before CellService#registerCellsTasks
        CellService.registerCellsTasks();
    }

    public void reload() {
        // Load lobby
        Location spawn = getConfig().getLocation("lobby");
        if (spawn != null) {
            ConfigDb.spawn = spawn;
        }

        // Load lobby on join
        PlayerListener.lobbyOnJoin = getConfig().getBoolean("teleport-join", false);

        ConfigDb.resetDelay = getConfig().getInt("mines.reset-delay", 6000);
    }

    private static void registerListeners(PluginManager manager) {
        //  Registering listeners  \\
        Listener[] listeners = new Listener[]{
                new BlockBreakListener(),
                new PlayerListener(),
                new EntityDamageListener(),
                new ExplosionListener()
        };

        for (Listener listener : listeners) {
            manager.registerEvents(listener, INSTANCE);
        }
    }

    private void registerPapi() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            texter.console("&aPlaceholderAPI found! Registering expansion...");
            new PlaceholderApiExpansion().register();
        } else {
            texter.console("&cPlaceholderAPI not found! Ignoring expansion...");
            //Bukkit.getPluginManager().disablePlugin(this);
        }
    }
}
