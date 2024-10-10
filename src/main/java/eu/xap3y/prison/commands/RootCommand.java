package eu.xap3y.prison.commands;

import eu.xap3y.prison.Prison;
import eu.xap3y.prison.api.enums.CellType;
import eu.xap3y.prison.api.enums.EnchantType;
import eu.xap3y.prison.api.gui.CellGui;
import eu.xap3y.prison.api.gui.MainGui;
import eu.xap3y.prison.api.gui.PrestigeGui;
import eu.xap3y.prison.api.interfaces.Debug;
import eu.xap3y.prison.services.BoardService;
import eu.xap3y.prison.services.CellService;
import eu.xap3y.prison.services.LevelService;
import eu.xap3y.prison.storage.ConfigDb;
import eu.xap3y.prison.storage.PlayerStorage;
import eu.xap3y.prison.storage.dto.Cell;
import eu.xap3y.prison.util.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.incendo.cloud.annotations.*;

import java.util.ArrayList;
import java.util.List;

public class RootCommand {

    @Command("prison")
    @CommandDescription("Main Prison command")
    public void rootCommand(CommandSender p0) {
        Prison.texter.response(p0, "Prison plugin for ravenode v" + Prison.ver + "  main: " + Prison.main);
    }

    @ProxiedBy("menu")
    @Command("prison menu")
    @CommandDescription("Open the prison menu")
    public void openMenu(
            CommandSender p0
    ) {
        if (!(p0 instanceof Player player)) {
            Prison.texter.response(p0, ConfigDb.ONLY_PLAYER);
            return;
        }

        MainGui.openGui(player);
    }

    @Command("prison setlobby")
    @CommandDescription("Set the lobby location")
    @Permission(value = {"prison.setlobby", "prison.*"}, mode = Permission.Mode.ANY_OF)
    public void setLobby(
            CommandSender p0
    ) {
        if (p0 instanceof Player player) {
            Prison.INSTANCE.getConfig().set("lobby", player.getLocation());
            ConfigDb.spawn = ((Player) p0).getLocation();
            Prison.INSTANCE.saveConfig();
            Prison.texter.response(p0, "Lobby set!");
        } else {
            Prison.texter.response(p0, ConfigDb.ONLY_PLAYER);
        }
    }

    @Command("prison setspawn [cell]")
    @CommandDescription("Set the spawn of mine")
    @Permission(value = {"prison.setspawn", "prison.*"}, mode = Permission.Mode.ANY_OF)
    public void setSpawn(
            CommandSender p0,
            @Argument("cell") CellType cell
    ) {
        if (!(p0 instanceof Player player)) {
            Prison.texter.response(p0, ConfigDb.ONLY_PLAYER);
            return;
        }
        else if (cell == null) {
            Prison.texter.response(p0, "&cInvalid mine type &7(&c/prison setspawn <cell>&7)");
            return;
        }

        CellService.cellMapper.get(cell).setSpawn(player.getLocation());

        Prison.INSTANCE.getConfig().set("mines." + cell.name().toLowerCase() + ".spawn", player.getLocation());
        Prison.INSTANCE.saveConfig();
        Prison.texter.response(p0, "&fSpawn set for mine &e" + cell.name().toLowerCase());
    }

    @Command("prison wand")
    @CommandDescription("Get the wand")
    @Permission(value = {"prison.wand", "prison.*"}, mode = Permission.Mode.ANY_OF)
    public void getWand(
            CommandSender p0
    ) {
        if (p0 instanceof Player player) {
            player.getInventory().addItem(ConfigDb.WAND);
            Prison.texter.response(p0, "&fWand added to your inventory");
        } else {
            Prison.texter.response(p0, ConfigDb.ONLY_PLAYER);
        }
    }

    @Command("prison createMine [cell]")
    @CommandDescription("Create mine")
    @Permission(value = {"prison.create", "prison.*"}, mode = Permission.Mode.ANY_OF)
    public void createCellMine(
            CommandSender p0,
            @Argument("cell") CellType cell
    ) {
        if (!(p0 instanceof Player)) {
            Prison.texter.response(p0, ConfigDb.ONLY_PLAYER);
            return;
        }

        else if (cell == null) {
            Prison.texter.response(p0, "&cInvalid mine type &7(&c/prison createMine <cell>&7)");
            return;
        }

        else if (ConfigDb.loc1 == null || ConfigDb.loc2 == null) {
            Prison.texter.response(p0, "&cPlease set the area with the wand first");
            return;
        }

        else if (!CellService.cellMapper.containsKey(cell)) {
            Prison.texter.response(p0, "&cUnknown mine type!");
            return;
        }

        else if (CellService.cellMapper.get(cell).getSpawn() == null) {
            Prison.texter.response(p0, "&cPlease set the spawn first");
            return;
        }

        Cell temp = CellService.cellMapper.get(cell);
        temp.setPos1(ConfigDb.loc1);
        temp.setPos2(ConfigDb.loc2);
        Prison.INSTANCE.getConfig().set("mines." + cell.name().toLowerCase() + ".area.loc1", ConfigDb.loc1);
        Prison.INSTANCE.getConfig().set("mines." + cell.name().toLowerCase() + ".area.loc2", ConfigDb.loc2);
        Prison.INSTANCE.saveConfig();
        Prison.texter.response(p0, "&fMining area set for mine &e" + cell.name().toLowerCase());
    }

    @Command("prison reset [cell]")
    @CommandDescription("Reset mine")
    @Permission(value = {"prison.reset", "prison.*"}, mode = Permission.Mode.ANY_OF)
    public void resetCellMine(
            CommandSender p0,
            @Argument("cell") CellType cell
    ) {
        if (!(p0 instanceof Player)) {
            Prison.texter.response(p0, ConfigDb.ONLY_PLAYER);
            return;
        }
        else if (cell == null) {
            Prison.texter.response(p0, "&cInvalid mine type &7(&c/prison reset <cell>&7)");
            return;
        }

        CellService.resetCell(cell);
        Prison.texter.response(p0, "&fMine &e" + cell.name().toLowerCase() + " &freset");
    }

    // This should not be used at all
    @Debug
    @Command("prison resetAll")
    @CommandDescription("Reset all cells")
    @Permission(value = {"prison.reset", "prison.*"}, mode = Permission.Mode.ANY_OF)
    public void resetAllCellsMine(
            CommandSender p0
    ) {
        if (!(p0 instanceof Player)) {
            Prison.texter.response(p0, ConfigDb.ONLY_PLAYER);
            return;
        }

        CellService.resetAllCells();

        Prison.texter.response(p0, "&fAll mines has been reset");
    }

    @ProxiedBy("level")
    @Command("prison level")
    @CommandDescription("Get your level")
    public void getLevel(
            CommandSender p0
    ) {
        if (!(p0 instanceof Player player)) {
            Prison.texter.response(p0, ConfigDb.ONLY_PLAYER);
            return;
        }

        double xpLeft = Utils.fixDecimals(LevelService.playerCache.get(player.getUniqueId()).getLeft() - PlayerStorage.economy.get(player.getUniqueId()).getXp());

        String progress = LevelService.shortProgress(player.getUniqueId(), 20, '&');

        double multiplier = LevelService.playerCache.getMultiplier(player.getUniqueId());
        int percent = (int) ((PlayerStorage.economy.get(player.getUniqueId()).getXp() / LevelService.playerCache.getRequiredXp(player.getUniqueId())) * 100);
        Prison.texter.response(p0, "&fYour level: &e" + PlayerStorage.economy.get(player.getUniqueId()).getLevel() + " &8(&e" + xpLeft + " &7XP left to level up&8) &8[&a" + multiplier + "x&8]");
        Prison.texter.response(p0, " &f⤷ Next level: &7❰ &r" + progress + " &7❱  &a" + percent + "%");
    }

    @Debug
    @Command("prison empty [cell]")
    @Permission(value = {"prison.debug", "prison.*"}, mode = Permission.Mode.ANY_OF)
    public void getLevel(
            CommandSender p0,
            @Argument("cell") CellType cellType
    ) {
        if (!(p0 instanceof Player)) {
            Prison.texter.response(p0, ConfigDb.ONLY_PLAYER);
            return;
        }
        else if (cellType == null) {
            Prison.texter.response(p0, "&cInvalid mine type &7(&c/prison empty <cell>&7)");
            return;
        }

        Cell cell = CellService.cellMapper.get(cellType);
        Prison.texter.response(p0, "IS CLEAR: " + cell.isEmpty());
    }

    @ProxiedBy("prestige")
    @Command("prison prestige")
    public void prestige(
            CommandSender p0
    ) {
        if (!(p0 instanceof Player player)) {
            Prison.texter.response(p0, ConfigDb.ONLY_PLAYER);
            return;
        }

        //Cell cell = CellService.cellMapper.get(cellType);
        PrestigeGui.openGui(player);
    }

    @Command("prison reload")
    @CommandDescription("Reload the plugins configuration")
    @Permission(value = {"prison.reload", "prison.*"}, mode = Permission.Mode.ANY_OF)
    public void reloadConfig(
            CommandSender p0
    ) {
        Prison.INSTANCE.reloadConfig();
        Prison.INSTANCE.reload();
        BoardService.loadConfig();
        BoardService.reloadAllBoards();
        Prison.texter.response(p0, "&fConfiguration reloaded");
    }

    @Debug
    @Command("prison giveTest [type]")
    public void giveTest(
            CommandSender p0,
            @Argument("type") EnchantType type
    ) {
        if (!(p0 instanceof Player player)) {
            Prison.texter.response(p0, ConfigDb.ONLY_PLAYER);
            return;
        }
        else if (type == null) {
            Prison.texter.response(p0, "&cInvalid enchant name &7(&c/prison giveTest <type>&7)");
            return;
        }

        //Cell cell = CellService.cellMapper.get(cellType);
        ItemStack modifiedItem = ConfigDb.getAdvancedPickaxe(type);
        ItemMeta meta = modifiedItem.getItemMeta();
        List<Component> listOfLore = new ArrayList<>() {{
            add(Component.empty());
            add(Component.text("§7Enchant: §b" + type.name().toUpperCase()));
        }};
        meta.lore(listOfLore);
        modifiedItem.setItemMeta(meta);
        player.getInventory().addItem(modifiedItem);
    }

    @Debug
    @Command("prison cheatLvl <level>")
    public void cheatLvl(
            CommandSender p0,
            @Argument("level") int level
    ) {
        if (!(p0 instanceof Player player)) {
            Prison.texter.response(p0, ConfigDb.ONLY_PLAYER);
            return;
        }

        PlayerStorage.economy.get(player.getUniqueId()).setLevel(level);
        PlayerStorage.savePlayers();
        BoardService.updateBoard(player.getUniqueId(), true);
        Prison.texter.response(p0, "Level set to " + level);
    }

    @Debug
    @Command("gmc")
    public void gamemodeC(
            CommandSender p0
    ) {
        if (!(p0 instanceof Player player)) {
            Prison.texter.response(p0, ConfigDb.ONLY_PLAYER);
            return;
        }

        Bukkit.getScheduler().runTask(Prison.INSTANCE, () -> player.setGameMode(GameMode.CREATIVE));
    }

    @Debug
    @Command("gms")
    public void gamemodeS(
            CommandSender p0
    ) {
        if (!(p0 instanceof Player player)) {
            Prison.texter.response(p0, ConfigDb.ONLY_PLAYER);
            return;
        }

        Bukkit.getScheduler().runTask(Prison.INSTANCE, () -> player.setGameMode(GameMode.SURVIVAL));
    }


    /*@Command("test")
    @CommandDescription("Test Prison command")
    public void testCommand(CommandSender p0) {
        if (p0 instanceof Player) {
            Location block = ((Player) p0).getLocation();

            for(int x = 0; x < 100; x++) {

                Random random = new Random();
                int chance = random.nextInt(100); // Generate a number between 0 and 99

                // Set the material based on the chance
                Material material;
                if (chance <= 50) {
                    material = Material.COBBLESTONE; // 50% chance for cobblestone
                } else if (chance <= 75) {
                    material = Material.COAL_BLOCK; // 25% chance for coal
                } else {
                    material = Material.STONE; // 25% chance for stone
                }

                Location loc = block.clone().add(x, 0, 0);
                Bukkit.getScheduler().runTask(Prison.INSTANCE, () -> loc.getBlock().setType(material));
            }
        } else {
            return;
        }
    }*/
}
