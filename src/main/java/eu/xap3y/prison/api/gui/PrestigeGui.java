package eu.xap3y.prison.api.gui;

import eu.xap3y.prison.Prison;
import eu.xap3y.prison.services.PrestigeService;
import eu.xap3y.prison.storage.PlayerStorage;
import eu.xap3y.skullcreator.SkullCreator;
import eu.xap3y.xagui.GuiMenu;
import eu.xap3y.xagui.models.GuiButton;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class PrestigeGui {

    public static void openGui(Player p0) {
        buildGui(p0).open(p0);
    }

    private static GuiMenu buildGui(Player p0) {
        GuiMenu gui = Prison.xagui.createMenu("&9&lPrestige", 5);

        gui.fillBorder();

        gui.addCloseButton();

        int prestiges = PlayerStorage.economy.get(p0.getUniqueId()).getPrestiges();

        gui.setSlot(4, StaticItems.getPrestigeMainItem()
                .setLore("", "&7You have &e" + prestiges + " &7prestiges")
                .setAmount(prestiges+1)
        );
        //22

        String[] text;

        boolean req = PlayerStorage.economy.get(p0.getUniqueId()).getLevel() >= 99;
        if (!req) {
            text = new String[]{"", "&c&l✗ &cYou need to be level 99"};
        } else {
            text = new String[]{"", "&2&l✔ &fLevel 99+", "", "&eClick to prestige"};
        }

        // TODO: Better lore
        gui.setSlot(22, new GuiButton(SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzc3ZDRhMjA2ZDc3NTdmNDc5ZjMzMmVjMWEyYmJiZWU1N2NlZjk3NTY4ZGQ4OGRmODFmNDg2NGFlZTdkM2Q5OCJ9fX0=")).setName("&9&lPrestige")
                .setLore(text)
                .withListener(inventoryClickEvent -> {
                    if (!req) {
                        p0.playSound(p0, Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                        return;
                    }

                    ConfirmGui.openGui(p0, "&9&lPrestige", StaticItems.getPrestigeMainItem().getItem(),
                            new GuiButton(StaticItems.getConfirmPane())
                                    .withListener(event -> {
                                        event.getWhoClicked().closeInventory();
                                        PrestigeService.prestige(p0);
                                    })
                    );
                })
        );

        return gui;
    }
}
