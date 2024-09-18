package eu.xap3y.prison;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.xap3y.prison.commands.RootCommand;
import eu.xap3y.prison.listeners.BlockBreakListener;
import eu.xap3y.prison.listeners.PlayerListener;
import eu.xap3y.prison.manager.CommandManager;
import eu.xap3y.prison.manager.ConfigManager;
import eu.xap3y.prison.services.CellService;
import eu.xap3y.prison.storage.PlayerStorage;
import eu.xap3y.prison.storage.StorageManager;
import eu.xap3y.xagui.XaGui;
import eu.xap3y.xalib.managers.Texter;
import eu.xap3y.xalib.objects.ProgressbarModifier;
import eu.xap3y.xalib.objects.TexterObj;
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

        StorageManager.saveBlocks();
        StorageManager.loadBlocks();

        /*for (Map.Entry<Material, Block> entry : StorageManager.getBlocks().entrySet()) {
            texter.console("&a" + entry.getKey().name() + " &7-Percentage &a" + entry.getValue().getPercentage() + " &7-XP &a" + entry.getValue().getXp() + " &7-COINS &a" + entry.getValue().getCoins());
        }*/

        PlayerStorage.loadPlayersFromStorage();

        registerListeners(getServer().getPluginManager());

        String test = Texter.progressBar(new ProgressbarModifier(80, '|', '|', "&a||", "&f||"));
        texter.console("80% = " + test);

        String test2 = Texter.progressBar(new ProgressbarModifier(20, '|', '|', "&a||", "&f||"));
        texter.console("20% = " + test2);

        CellService.loadCellsLocs();
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

    /*private void registerPapi() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            *//*
             * We register the EventListener here, when PlaceholderAPI is installed.
             * Since all events are in the main class (this class), we simply use "this"
             *//*
            //Bukkit.getPluginManager().registerEvents(new MyListener(), this);
        } else {
            *//*
             * We inform about the fact that PlaceholderAPI isn't installed and then
             * disable this plugin to prevent issues.
             *//*
            //Bukkit.getPluginManager().disablePlugin(this);
        }
    }*/
}
