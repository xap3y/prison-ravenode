package eu.xap3y.prison.listeners;

import eu.xap3y.prison.api.gui.ItemUpgraderGui;
import eu.xap3y.prison.api.gui.MainGui;
import eu.xap3y.prison.api.gui.StaticItems;
import eu.xap3y.prison.services.BoardService;
import eu.xap3y.prison.services.LevelService;
import eu.xap3y.prison.storage.ConfigDb;
import eu.xap3y.prison.storage.PlayerStorage;
import eu.xap3y.xalib.managers.Texter;
import org.apache.commons.lang3.tuple.MutablePair;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class PlayerListener implements Listener {

    public static boolean lobbyOnJoin = false;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!PlayerStorage.economy.containsKey(event.getPlayer().getUniqueId())) {
            PlayerStorage.loadPlayerFromStorage(event.getPlayer().getUniqueId());
        }

        event.getPlayer().setLevel(PlayerStorage.economy.get(event.getPlayer().getUniqueId()).getLevel());
        LevelService.playerCache.put(event.getPlayer().getUniqueId(),
                new MutablePair<>(
                        LevelService.requiredXp(PlayerStorage.economy.get(event.getPlayer().getUniqueId()).getLevel()+1),
                        LevelService.getMultiplier(event.getPlayer().getUniqueId())
                )
        );

        if (!event.getPlayer().hasPlayedBefore() || event.getPlayer().getInventory().isEmpty()) {
            event.getPlayer().getInventory().setItem(0, ConfigDb.getDefaultPickaxe(1));
        }

        event.getPlayer().getInventory().setItem(8, StaticItems.getMainMenuItem());

        BoardService.addBoard(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        BoardService.removeBoard(event.getPlayer().getUniqueId());
        LevelService.playerCache.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onChat(PlayerChatEvent event) {
        event.setFormat(Texter.colored("&7[&b" + PlayerStorage.economy.get(event.getPlayer().getUniqueId()).getLevel() + "&7] &r%1$s &7➽ &r%2$s"));
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        if (event.getItem() != null && event.getItem().isSimilar(StaticItems.getMainMenuItem())) {
            event.setCancelled(true);
            MainGui.openGui(event.getPlayer());
            return;
        }

        if (event.getItem() == null || !event.getAction().isRightClick() || !event.getPlayer().isSneaking()) return;

        ItemStack item = event.getItem();
        ItemMeta meta = item.getItemMeta();

        if (meta == null) return;

        Integer level = meta.getPersistentDataContainer().get(ConfigDb.PRISON_TOOL_LEVEL_KEY, PersistentDataType.INTEGER);

        if (level == null) return;

        ItemUpgraderGui.buildGui(item.clone()).open(event.getPlayer());

        event.getItem().setAmount(0);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInventoryClick(InventoryClickEvent event) {

        if (event.getClickedInventory() == null) return;

        int slotClicked = event.getRawSlot();
        if(slotClicked < event.getClickedInventory().getSize()) {
            return;
        }

        if (event.getSlot() == 8) {
            event.setCancelled(true);
            Player p0 = (Player) event.getWhoClicked();
            p0.closeInventory();
            MainGui.openGui(p0);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().isSimilar(StaticItems.getMainMenuItem())) {
            event.setCancelled(true);
        }
    }
}
