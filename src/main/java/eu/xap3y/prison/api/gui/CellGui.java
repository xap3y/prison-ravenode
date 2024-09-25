package eu.xap3y.prison.api.gui;

import eu.xap3y.prison.Prison;
import eu.xap3y.prison.services.CellService;
import eu.xap3y.xagui.GuiMenu;
import eu.xap3y.xagui.models.GuiButton;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class CellGui {

    private static GuiMenu buildGui() {
        GuiMenu gui = Prison.xagui.createMenu("&f&lCells", 5);

        gui.fillBorder();

        gui.addCloseButton();

        AtomicInteger slot = new AtomicInteger(10);
        CellService.cellMapper.values().forEach(cell -> {
            GuiButton temp = new GuiButton(StaticItems.headMapper.get(cell.getType()).clone())
                    .setName(cell.getName())
                    .setLore("&r", "&2&l✔ &2Unlocked", " ", "&7Click to teleport")
                    .withListener((inventoryClickEvent -> {
                        Bukkit.getScheduler().runTask(Prison.INSTANCE, () -> {
                            inventoryClickEvent.getWhoClicked().teleport(cell.getSpawn());
                        });
                    }));

            gui.setSlot(slot.get(), temp);
            slot.getAndIncrement();
        });

        return gui;
    }

    public static void openGui(Player p0) {
        buildGui().open(p0);
    }
}
