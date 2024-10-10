package eu.xap3y.prison.api.gui;

import eu.xap3y.prison.Prison;
import eu.xap3y.prison.api.enums.CellType;
import eu.xap3y.prison.api.enums.LeaderBoardType;
import eu.xap3y.prison.services.CellService;
import eu.xap3y.prison.storage.ConfigDb;
import eu.xap3y.prison.storage.PlayerStorage;
import eu.xap3y.prison.util.Utils;
import eu.xap3y.skullcreator.SkullCreator;
import eu.xap3y.xagui.models.GuiButton;
import it.unimi.dsi.fastutil.Hash;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class StaticItems {

    public static HashMap<CellType, ItemStack> headMapper = new HashMap<>();

    // Loading it before displaying the GUI to avoid any possible delays between the command being send and GUI opening
    public static void init() {
        CellService.cellMapper.values().forEach(cell -> {
            headMapper.put(cell.getType(), SkullCreator.itemFromBase64(cell.getHead()));
        });
    }

    public static GuiButton getPrestigeMainItem() {
        return new GuiButton(Material.ENDER_EYE)
                .setName("&9&lPrestige");
    }

    public static ItemStack getConfirmPane() {
        return new GuiButton(Material.LIME_STAINED_GLASS_PANE).setName("&a&lConfirm").getItem();
    }

    public static GuiButton getRejectPane() {
        return new GuiButton(Material.RED_STAINED_GLASS_PANE).setName("&c&lReject").withListener((inventoryClickEvent) -> {
            inventoryClickEvent.getWhoClicked().closeInventory();
        });
    }

    public static GuiButton getBackButton() {
        return new GuiButton(SkullCreator.itemFromBase64(ConfigDb.BACK))
                .setName("&c&lGo Back")
                .withListener(inventoryClickEvent -> {
                    Player p0 = (Player) inventoryClickEvent.getWhoClicked();
                    p0.playSound(p0, Sound.BLOCK_LEVER_CLICK, 1f, 1f);
                    MainGui.openGui(p0);
                });
    }

     ////////////////////////////////////////////////////////////
    //                      Main GUI items                    //
   ////////////////////////////////////////////////////////////

    public static GuiButton getPrestigeItem() {
        return new GuiButton(Material.ENDER_EYE)
                .setName("&9&lPrestige")
                .setLore(" ", "&eClick to open")
                .withListener(inventoryClickEvent -> {
                    Player p0 = (Player) inventoryClickEvent.getWhoClicked();
                    p0.playSound(p0, Sound.BLOCK_LEVER_CLICK, 1f, 1f);
                    PrestigeGui.openGui(p0);
                });
    }

    public static GuiButton getMinesItem() {
        return new GuiButton(Material.BOOKSHELF)
                .setName("&f&lCells")
                .setLore(" ", "&eClick to open")
                .withListener(inventoryClickEvent -> {
                    Player p0 = (Player) inventoryClickEvent.getWhoClicked();
                    p0.playSound(p0, Sound.BLOCK_LEVER_CLICK, 1f, 1f);
                    CellGui.openGui(p0);
                });
    }

    public static GuiButton getCratesItem() {

        return new GuiButton(SkullCreator.itemFromBase64(ConfigDb.CRATES))
                .setName("&9&lCrates")
                .setLore(" ", "&eClick to open");
    }

    public static GuiButton getToolUpgrades() {

        return new GuiButton(Material.ANVIL)
                .setName("&c&lTool upgrading")
                .setLore(" ", "&eClick to open");
    }

    public static ItemStack getLeaderBoardItem(LeaderBoardType type) {

        String[] numbering = {"&e①", "&6②", "&c③", "&f④", "&f⑤"};
        LinkedHashMap<UUID, Integer> sortedPlayers = Utils.getLeaderboard(type);

        List<String> lore= new ArrayList<>();
        lore.add("");
        lore.add("&7-+------------------+-");

        int i = 0;
        for (Map.Entry<UUID, Integer> temp : sortedPlayers.entrySet()) {
            //Prison.texter.console(sortedPlayers[i] + " IS " + Bukkit.getPlayer(sortedPlayers[i]).getName());
            lore.add(numbering[i] + " &f" + Bukkit.getOfflinePlayer(temp.getKey()).getName() + " &7- &e" + temp.getValue());
            i++;
            //lore[1] = numbering[0] + " &f" + Bukkit.getPlayer((UUID) sortedPlayers.keySet().toArray()[0]).getName() + " &7- &e" + Utils.fixDecimals(PlayerStorage.economy.get((UUID) sortedPlayers.keySet().toArray()[0]).getLevel());
        }

        lore.add("&7-+------------------+-");
        lore.add("");
        for (LeaderBoardType val : LeaderBoardType.values()) {
            if (val != type) {
                lore.add("  &c" + val.name());
            } else {
                lore.add(" &3➸ &a" + val.name());
            }
        }


        return new GuiButton(Material.OAK_SIGN)
                .setName("&6&lLeaderboard &f(" + type.name() + ")")
                .setLoreList(lore)
                .getItem();
    }
}
