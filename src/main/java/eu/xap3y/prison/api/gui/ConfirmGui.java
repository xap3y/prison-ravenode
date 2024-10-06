package eu.xap3y.prison.api.gui;

import eu.xap3y.prison.Prison;
import eu.xap3y.xagui.GuiMenu;
import eu.xap3y.xagui.XaGui;
import eu.xap3y.xagui.models.GuiButton;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ConfirmGui {

    public static void openGui(Player p0, String title, ItemStack item, GuiButton button) {
        buildGui(title, item, button).open(p0);
    }

    private static GuiMenu buildGui(String title, ItemStack item, GuiButton button) {
        GuiMenu gui = Prison.xagui.createMenu(title, 3);

        gui.fillBorder();

        gui.setSlot(4, item);

        gui.setSlot(11, button);

        gui.setSlot(10, XaGui.Companion.getBorderFiller());
        gui.setSlot(12, XaGui.Companion.getBorderFiller());
        gui.setSlot(13, XaGui.Companion.getBorderFiller());
        gui.setSlot(14, XaGui.Companion.getBorderFiller());
        gui.setSlot(16, XaGui.Companion.getBorderFiller());

        gui.setSlot(15, StaticItems.getRejectPane());

        gui.addCloseButton();

        return gui;
    }
}
