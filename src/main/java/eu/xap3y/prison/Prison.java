package eu.xap3y.prison;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.xap3y.prison.api.enchants.TestEnchant;
import eu.xap3y.prison.api.gui.StaticItems;
import eu.xap3y.prison.commands.RootCommand;
import eu.xap3y.prison.listeners.BlockBreakListener;
import eu.xap3y.prison.listeners.PlaceholderApiExpansion;
import eu.xap3y.prison.listeners.PlayerListener;
import eu.xap3y.prison.manager.CommandManager;
import eu.xap3y.prison.manager.ConfigManager;
import eu.xap3y.prison.manager.EnchantManager;
import eu.xap3y.prison.services.CellService;
import eu.xap3y.prison.storage.PlayerStorage;
import eu.xap3y.prison.storage.StorageManager;
import eu.xap3y.prison.util.LogLogger;
import eu.xap3y.xagui.XaGui;
import eu.xap3y.xalib.managers.Texter;
import eu.xap3y.xalib.objects.TexterObj;
import org.bukkit.Bukkit;
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

    @Override
    public void onEnable() {
        INSTANCE = this;


        //  Initializing XaGUI  \\
        xagui = new XaGui(this);

        //  Creating parser & Parsing command classes below  \\
        CommandManager cmdManager = new CommandManager();
        cmdManager.parse(new RootCommand());

        //  Saving if not exists & Reloading config file  \\
        ConfigManager.reloadConfig();

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

        CellService.registerCellsTasks();

        EnchantManager.registerEnchant("test", new TestEnchant());
    }

    private static void registerListeners(PluginManager manager) {
        //  Registering listeners  \\
        Listener[] listeners = new Listener[]{
                new BlockBreakListener(),
                new PlayerListener(),
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

            //Bukkit.getPluginManager().disablePlugin(this);
        }
    }
}
