package eu.xap3y.prison.listeners;

import eu.xap3y.prison.api.gui.ItemUpgraderGui;
import eu.xap3y.prison.services.BoardService;
import eu.xap3y.prison.services.LevelService;
import eu.xap3y.prison.storage.ConfigDb;
import eu.xap3y.prison.storage.PlayerStorage;
import eu.xap3y.xalib.managers.Texter;
import org.apache.commons.lang3.tuple.MutablePair;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
            event.getPlayer().getInventory().setItem(0, ConfigDb.getDefaultPickaxe(0));
        }

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
        if (event.getItem() == null || !event.getAction().isRightClick() || !event.getPlayer().isSneaking()) return;

        ItemStack item = event.getItem();
        ItemMeta meta = item.getItemMeta();

        if (meta == null) return;

        Integer level = meta.getPersistentDataContainer().get(ConfigDb.PRISON_TOOL_LEVEL_KEY, PersistentDataType.INTEGER);

        if (level == null) return;

        ItemUpgraderGui.buildGui(item.clone()).open(event.getPlayer());

        event.getItem().setAmount(0);
    }
}
