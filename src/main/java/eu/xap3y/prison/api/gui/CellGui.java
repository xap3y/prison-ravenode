package eu.xap3y.prison.api.gui;

import eu.xap3y.prison.Prison;
import eu.xap3y.prison.api.enums.CellType;
import eu.xap3y.prison.services.CellService;
import eu.xap3y.xagui.GuiMenu;
import eu.xap3y.xagui.models.GuiButton;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class CellGui {

    private static GuiMenu buildGui(UUID p0) {
        GuiMenu gui = Prison.xagui.createMenu("&f&lCells", 5);

        gui.fillBorder();

        gui.addCloseButton();

        gui.setSlot(36, StaticItems.getBackButton());

        AtomicInteger slot = new AtomicInteger(10);
        CellService.cellMapper.values().forEach(cell -> {

            ArrayList<String> loreBuilder = new ArrayList<>();
            loreBuilder.add(" ");
            if (cell.getType() != CellType.STARTER) {
                loreBuilder.add("&7Min. Level: &f" + cell.getMinLevel());
                loreBuilder.add(" ");
            }
            boolean unlocked = CellService.hasUnlockedCell(p0, cell.getType());
            String status = unlocked ? "&2&l✔ &2Unlocked" : "&4&l✘ &4Locked";
            loreBuilder.add(status);
            if (unlocked) {
                loreBuilder.add(" ");
                loreBuilder.add("&7Click to teleport");
            }

            GuiButton temp = new GuiButton(StaticItems.headMapper.get(cell.getType()).clone())
                    .setName(cell.getName())
                    .setLoreList(loreBuilder)
                    .withListener((inventoryClickEvent -> {
                        Player p = (Player) inventoryClickEvent.getWhoClicked();
                        if (unlocked) {
                            Bukkit.getScheduler().runTask(Prison.INSTANCE, () -> {
                                p.teleport(cell.getSpawn());
                            });
                            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_FLUTE, 1f, 1f);

                        } else {
                            p.playSound(p, Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                        }
                    }));

            gui.setSlot(slot.get(), temp);
            slot.getAndIncrement();
        });

        return gui;
    }

    public static void openGui(Player p0) {
        buildGui(p0.getUniqueId()).open(p0);
    }
}
