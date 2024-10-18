package eu.xap3y.prison.api.gui;

import eu.xap3y.prison.Prison;
import eu.xap3y.prison.api.enums.UpgradeSolution;
import eu.xap3y.prison.storage.ConfigDb;
import eu.xap3y.prison.storage.PlayerStorage;
import eu.xap3y.prison.storage.dto.ToolDto;
import eu.xap3y.prison.storage.dto.UpgradeRes;
import eu.xap3y.prison.util.Utils;
import eu.xap3y.xagui.GuiMenu;
import eu.xap3y.xagui.XaGui;
import eu.xap3y.xagui.models.GuiButton;
import lombok.SneakyThrows;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;

public class ItemUpgraderGui {

    private static final HashSet<Integer> statusSlots = new HashSet<>() {{
        add(0);
        add(9);
        add(18);
        add(27);
        add(8);
        add(17);
        add(26);
        add(35);
        add(44);
    }};

    private static final Integer[] freeSlots = {10, 11, 12, 28, 29, 30, 14, 15, 16, 32, 33, 34, 19, 21, 23, 25};

    private static final ItemStack greenStatus = new GuiButton(Material.LIME_STAINED_GLASS_PANE).setName("&r").getItem();
    private static final ItemStack whitePane = new GuiButton(Material.WHITE_STAINED_GLASS_PANE).setName("&r").getItem();
    private static final ItemStack redStatus = new GuiButton(Material.RED_STAINED_GLASS_PANE).setName("&r").getItem();

    public static GuiMenu buildGui() {
        return buildGui(null);
    }

    @SneakyThrows
    public static GuiMenu buildGui(@Nullable ItemStack item) {
        GuiMenu gui = Prison.xagui.createMenu("&fItem Upgrader", 5);

        gui.fillBorder();

        gui.setSelfInventoryAccess(true);
        gui.allowSelfInventoryClickTypes(ClickType.LEFT, ClickType.RIGHT);

        GuiButton forgeButton = new GuiButton(Material.ANVIL)
                .setName("&a&lForge")
                .setLore("", "&cPut your tool in the slot");

        gui.setSlot(22, forgeButton);

        gui.setSlot(13, XaGui.Companion.getBorderFiller());
        gui.setSlot(31, XaGui.Companion.getBorderFiller());

        gui.fillSlotsArr(freeSlots, whitePane);

        AtomicReference<ItemStack> itemToUpgrade = new AtomicReference<>();

        if (item != null) {
            gui.setSlot(20, item.clone());
            itemToUpgrade.set(item);
            fireChangeState(gui, getUpgradeRes(item));
        }

        gui.setOnClick((e) -> {

            //Prison.texter.console("Slot: " + e.getSlot());
            //Prison.texter.console("Cursor " + e.getCursor().getType());
            //Prison.texter.console("CurrentItem " + Objects.requireNonNull(e.getCurrentItem()).getType());

            if (e.getSlot() == 20) {
                e.setCancelled(true);
                if (gui.getSlot(20) != null && itemToUpgrade.get() != null) {
                    ItemStack itemGui = gui.getSlot(20).getItem();
                    e.getWhoClicked().getInventory().addItem(itemGui.clone());
                    gui.updateSlot(20, Material.AIR);
                    itemToUpgrade.set(null);
                    fireChangeState(gui, null);
                    gui.fillSlots(statusSlots, XaGui.Companion.getBorderFiller());
                }
                else if (e.getCursor().getItemMeta() != null) {

                    if (gui.getSlot(24) != null) {
                        e.setCancelled(true);
                        return;
                    }

                    itemToUpgrade.set(e.getCursor().clone());
                    gui.setSlot(20, e.getCursor().clone());
                    fireChangeState(gui, getUpgradeRes(e.getCursor()));
                    e.getCursor().setAmount(0);
                }
            }
        });
        gui.addCloseButton();

        gui.setSlot(36, StaticItems.getBackButton());

        gui.setOnClose((e) -> {
            if (gui.getSlot(20) != null) {
               e.getPlayer().getInventory().addItem(gui.getSlot(20).getItem());
           }
        });

        return gui;
    }

    private static void fireChangeState(@NotNull GuiMenu gui, @Nullable UpgradeRes res) {

        if (res == null) {
            gui.setSlot(22, ConfigDb.upgradeResMapper.get(UpgradeSolution.NO_ITEM).getButton());
            gui.clearSlot(24);
            gui.fillSlots(statusSlots, XaGui.Companion.getBorderFiller());
            return;
        }

        if (res.isUpgradable()) {

            gui.fillSlots(statusSlots, greenStatus);

            ToolDto toolDto = res.getTool();
            toolDto.setLevel(toolDto.getLevel()+1);
            toolDto.setName(String.format(ConfigDb.TOOL_NAME_PATTERN, "Iron Pickaxe", Utils.intToRoman(toolDto.getLevel())));
            ItemStack newItem = Utils.constructTool(toolDto);

            // Create preview item
            gui.setSlot(24, newItem);

            GuiButton button = res.getButton();
            button = button.setLore(" ", "&7Cost: &a" + res.getCost() + "$", "&7Level &c" + (res.getNewLevel()-1) + " &f➟ &b" + res.getNewLevel(), "", "&eClick to upgrade");
            button = button.withListener((e) -> {
                Player whoClicked = (Player) e.getWhoClicked();
                int eco =  (int) PlayerStorage.economy.get(whoClicked.getUniqueId()).getCoins();
                if (eco < res.getCost()) {
                    gui.close(whoClicked);
                    Utils.displayAction(whoClicked, "&cYou don't have enough coins!", 20);
                    whoClicked.playSound(whoClicked, Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                    return;
                }

                // Check for free space in player's inventory
                boolean hasSpace = false;
                for (int j = 0; j < whoClicked.getInventory().getSize()-5; j++) {
                    if (whoClicked.getInventory().getItem(j) == null) {
                        hasSpace = true;
                        break;
                    }
                }

                if (!hasSpace) {
                    gui.close(whoClicked);
                    Utils.displayAction(whoClicked, "&cYou don't have enough space in your inventory!", 20);
                    whoClicked.playSound(whoClicked.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1);
                    return;
                }

                whoClicked.getInventory().addItem(newItem.clone());
                whoClicked.playSound(whoClicked, Sound.BLOCK_ANVIL_USE, 1f, 1f);
                gui.clearSlot(20);
                gui.clearSlot(24);
                gui.fillSlots(statusSlots, XaGui.Companion.getBorderFiller());
                gui.setSlot(22, ConfigDb.upgradeResMapper.get(UpgradeSolution.NO_ITEM).getButton());
            });
            gui.setSlot(22, button);
        } else {
            gui.fillSlots(statusSlots, redStatus);
            gui.setSlot(22, res.getButton());
        }

    }

    /*private static @NotNull GuiButton getGuiButton(@NotNull GuiMenu gui, @NotNull UpgradeRes res) {
        GuiButton button = res.getButton();
        button = button.setLore(" ", "&7Cost: &a" + res.getCost() + "$", "&7Level &c" + (res.getNewLevel()-1) + " &f➟ &b" + res.getNewLevel(), "", "&eClick to upgrade");
        button = button.withListener((e) -> {
            Player whoClicked = (Player) e.getWhoClicked();
            int eco =  (int) PlayerStorage.economy.get(whoClicked.getUniqueId()).getCoins();
            if (eco < res.getCost()) {
                gui.close(whoClicked);
                Utils.displayAction(whoClicked, "&cYou don't have enough coins!", 20);
                whoClicked.playSound(whoClicked, Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                return;
            }

            // Check for free space in player's inventory
            boolean hasSpace = false;
            for (int j = 0; j < whoClicked.getInventory().getSize()-5; j++) {
                if (whoClicked.getInventory().getItem(j) == null) {
                    hasSpace = true;
                    break;
                }
            }

            if (!hasSpace) {
                gui.close(whoClicked);
                Utils.displayAction(whoClicked, "&cYou don't have enough space in your inventory!", 20);
                whoClicked.playSound(whoClicked.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1);
                return;
            }

            gui.clearSlot(20);
            gui.clearSlot(24);
            gui.fillSlots(statusSlots, XaGui.Companion.getBorderFiller());
            gui.setSlot(22, ConfigDb.upgradeResMapper.get(UpgradeSolution.NO_ITEM).getButton());
            ToolDto tool = res.getTool();
            whoClicked.getInventory().addItem(Utils.constructTool(res.getTool()));
        });
        return button;
    }*/

    private static UpgradeRes getUpgradeRes(@Nullable ItemStack item) {

        if (item == null) {
            return ConfigDb.upgradeResMapper.get(UpgradeSolution.NO_ITEM);
        }

        ToolDto tool = Utils.deserializeTool(item);

        if (tool == null) {
            return ConfigDb.upgradeResMapper.get(UpgradeSolution.INVALID_ITEM);
        }

        if (tool.getLevel() >= 10) {
            return ConfigDb.upgradeResMapper.get(UpgradeSolution.MAX_LEVEL);
        }

        UpgradeRes res = ConfigDb.upgradeResMapper.get(UpgradeSolution.ALLOWED);
        res.setNewLevel(tool.getLevel() + 1);
        res.setCost(res.getNewLevel() * 100);
        res.setTool(tool);
        return res;
    }
}
