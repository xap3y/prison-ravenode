package eu.xap3y.prison.api.gui;

import eu.xap3y.prison.Prison;
import eu.xap3y.prison.api.enums.LeaderBoardType;
import eu.xap3y.prison.storage.PlayerStorage;
import eu.xap3y.prison.util.Utils;
import eu.xap3y.skullcreator.SkullCreator;
import eu.xap3y.xagui.GuiMenu;
import eu.xap3y.xagui.models.GuiButton;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.atomic.AtomicReference;

import static eu.xap3y.prison.api.gui.StaticItems.getLeaderBoardItem;

public class MainGui {


    public static void openGui(Player p0) {
        buildGui(p0).open(p0);
    }

    private static GuiMenu buildGui(Player p0) {
        GuiMenu gui = Prison.xagui.createMenu("&6&lPrison", 6);

        gui.fillBorder();

        gui.addCloseButton();

        GuiButton mainButton = new GuiButton(SkullCreator.itemFromUuid(p0.getUniqueId()))
                .setName("&b" + p0.getName())
                .setLore(
                        " ",
                        "&7&l• &fʟᴇᴠᴇʟ: &b" + PlayerStorage.economy.get(p0.getUniqueId()).getLevel(),
                        "&7&l• &fᴘᴜʀꜱᴇ: &a" + (int) PlayerStorage.economy.get(p0.getUniqueId()).getCoins() + "$",
                        "&7&l• &fᴘʀᴇꜱᴛɪɢᴇꜱ: &9" + PlayerStorage.economy.get(p0.getUniqueId()).getPrestiges(),
                        "&7&l• &fʙʟᴏᴄᴋꜱ ᴍɪɴᴇᴅ: &9" + PlayerStorage.economy.get(p0.getUniqueId()).getBlocksMined()
                );

        gui.setSlot(4, mainButton);

        // Redirection buttons
        gui.setSlot(20, StaticItems.getMinesItem());
        gui.setSlot(22, StaticItems.getPrestigeItem());

        gui.setSlot(30, StaticItems.getCratesItem()
                .withListener((e) -> {
                    Player p1 = (Player) e.getWhoClicked();
                    p1.playSound(p1.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                    Prison.texter.response(p1, "&c&lCrates are not available yet!");
                })
        );

        gui.setSlot(32, StaticItems.getToolUpgrades()
                .withRedirect(ItemUpgraderGui::buildGui)
        );


        // dynamic leaderboard
        AtomicReference<LeaderBoardType> type = new AtomicReference<>(LeaderBoardType.LEVEL); // default
        ItemStack item = getLeaderBoardItem(type.get());
        AtomicReference<LeaderBoardType> next = new AtomicReference<>(Utils.getNextLeaderBoardType(type.get()));

        GuiButton button = new GuiButton(item.clone())
                .withListener((e) -> {
                    Player p1 = (Player) e.getWhoClicked();
                    p1.playSound(p1.getLocation(), Sound.BLOCK_LAVA_POP, 1f, 1f);
                    gui.updateSlot(24, getLeaderBoardItem(next.get()));
                    type.set(next.get());
                    next.set(Utils.getNextLeaderBoardType(type.get()));
                });

        gui.setSlot(24, button);

        return gui;
    }
}
