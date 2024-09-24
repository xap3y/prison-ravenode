package eu.xap3y.prison.listeners;

import eu.xap3y.prison.Prison;
import eu.xap3y.prison.services.BoardService;
import eu.xap3y.prison.services.LevelService;
import eu.xap3y.prison.storage.PlayerStorage;
import org.apache.commons.lang3.tuple.MutablePair;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

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

        BoardService.addBoard(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        BoardService.removeBoard(event.getPlayer().getUniqueId());
        LevelService.playerCache.remove(event.getPlayer().getUniqueId());
    }
}
