package eu.xap3y.prison.api.gui;

import eu.xap3y.prison.api.enums.CellType;
import eu.xap3y.prison.services.CellService;
import eu.xap3y.skullcreator.SkullCreator;
import eu.xap3y.xagui.models.GuiButton;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

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
}
