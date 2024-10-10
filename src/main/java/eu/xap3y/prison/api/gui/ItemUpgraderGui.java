package eu.xap3y.prison.api.gui;

import eu.xap3y.prison.Prison;
import eu.xap3y.prison.api.enums.UpgradeSolution;
import eu.xap3y.prison.storage.ConfigDb;
import eu.xap3y.prison.storage.dto.UpgradeRes;
import eu.xap3y.xagui.GuiMenu;
import eu.xap3y.xagui.XaGui;
import eu.xap3y.xagui.exceptions.SlotOutOfBoundException;
import eu.xap3y.xagui.models.GuiButton;
import lombok.SneakyThrows;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;

public class ItemUpgraderGui {

    private static final HashSet<Integer> statusSlots = new HashSet<Integer>() {{
        add(10);
        add(12);
        add(14);
        add(16);
    }};
    private static final ItemStack greenStatus = new GuiButton(Material.LIME_STAINED_GLASS_PANE).setName("").getItem();

    public static GuiMenu buildGui() {
        return buildGui(null);
    }

    @SneakyThrows
    public static GuiMenu buildGui(@Nullable ItemStack item) {
        GuiMenu gui = Prison.xagui.createMenu("&fItem Upgrader", 3);

        gui.fillBorder();

        gui.setSelfInventoryAccess(true);
        gui.allowSelfInventoryClickTypes(ClickType.LEFT, ClickType.RIGHT);

        GuiButton forgeButton = new GuiButton(Material.ANVIL)
                .setName("&a&lForge")
                .setLore("", "&cPut your tool in the slot")
                .withListener((e) -> {
                    try {
                        gui.lockButton(11);
                    } catch (SlotOutOfBoundException ex) {
                        // IGNORE
                    }


                });

        gui.setSlot(13, forgeButton);
        gui.setSlot(10, XaGui.Companion.getBorderFiller());
        gui.setSlot(12, XaGui.Companion.getBorderFiller());
        gui.setSlot(14, XaGui.Companion.getBorderFiller());
        gui.setSlot(16, XaGui.Companion.getBorderFiller());

        AtomicReference<ItemStack> itemToUpgrade = new AtomicReference<>();

        if (item != null) {
            gui.setSlot(11, item.clone());
            itemToUpgrade.set(item);
            fireChangeState(gui, getUpgradeRes(item));
        }

        gui.setOnClick((e) -> {

            //Prison.texter.console("Slot: " + e.getSlot());
            //Prison.texter.console("Cursor " + e.getCursor().getType());
            //Prison.texter.console("CurrentItem " + Objects.requireNonNull(e.getCurrentItem()).getType());

            if (e.getSlot() == 11) {
                e.setCancelled(true);
                if (gui.getSlot(11) != null && itemToUpgrade.get() != null) {
                    ItemStack itemGui = gui.getSlot(11).getItem();
                    e.getWhoClicked().getInventory().addItem(itemGui.clone());
                    gui.updateSlot(11, Material.AIR);
                    itemToUpgrade.set(null);
                    fireChangeState(gui, null);
                }
                else if (e.getCursor().getItemMeta() != null) {
                    itemToUpgrade.set(e.getCursor().clone());
                    gui.setSlot(11, e.getCursor().clone());
                    fireChangeState(gui, getUpgradeRes(e.getCursor()));
                    e.getCursor().setAmount(0);
                }
            }
        });
        gui.addCloseButton();

        gui.setOnClose((e) -> {
            if (gui.getSlot(11) != null) {
               e.getPlayer().getInventory().addItem(gui.getSlot(11).getItem());
           }
        });

        return gui;
    }

    private static void fireChangeState(@NotNull GuiMenu gui, @Nullable UpgradeRes res) {

        if (res == null) {
            gui.setSlot(13, ConfigDb.upgradeResMapper.get(UpgradeSolution.NO_ITEM).getButton());
            gui.clearSlot(15);
            gui.fillSlots(statusSlots, XaGui.Companion.getBorderFiller());
            return;
        }

        if (res.isUpgradable()) {
            gui.fillSlots(statusSlots, greenStatus);

            // Create preview item
            ItemStack newItem = gui.getSlot(11).getItem().clone();
            ItemMeta meta = newItem.getItemMeta();
            meta.getPersistentDataContainer().set(ConfigDb.PRISON_TOOL_LEVEL_KEY, PersistentDataType.INTEGER, res.getNewLevel());
            newItem.setItemMeta(meta);
            ItemStack temp = ConfigDb.getDefaultPickaxe(res.getNewLevel()-1); // -1 cuz indexed from 0
            gui.setSlot(15, temp);
        }

        gui.setSlot(13, res.getButton().addLore(" ", "&fCost: &a" + res.getCost() + "$", " ", "&eClick to upgrade"));

    }

    private static UpgradeRes getUpgradeRes(@Nullable ItemStack item) {

        if (item == null) {
            return ConfigDb.upgradeResMapper.get(UpgradeSolution.NO_ITEM);
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return ConfigDb.upgradeResMapper.get(UpgradeSolution.INVALID_ITEM);
        }

        Integer level = meta.getPersistentDataContainer().get(ConfigDb.PRISON_TOOL_LEVEL_KEY, PersistentDataType.INTEGER);

        if (level == null) {
            return ConfigDb.upgradeResMapper.get(UpgradeSolution.INVALID_ITEM);
        }

        if (level >= 10) {
            return ConfigDb.upgradeResMapper.get(UpgradeSolution.MAX_LEVEL);
        }

        UpgradeRes res = ConfigDb.upgradeResMapper.get(UpgradeSolution.ALLOWED);
        res.setNewLevel(level + 1);
        res.setCost(res.getNewLevel() * 100);
        return res;
    }
}
