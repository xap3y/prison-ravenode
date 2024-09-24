package eu.xap3y.prison.listeners;

import eu.xap3y.prison.Prison;
import eu.xap3y.prison.services.BoardService;
import eu.xap3y.prison.services.LevelService;
import eu.xap3y.prison.storage.ConfigDb;
import eu.xap3y.prison.storage.PlayerStorage;
import eu.xap3y.prison.storage.StorageManager;
import eu.xap3y.prison.storage.dto.Block;
import eu.xap3y.prison.util.Utils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class BlockBreakListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent event) {
        // Check if in mine

        if (
                event.getPlayer().getInventory().getItemInMainHand().getType() == Material.BLAZE_ROD &&
                        event.getPlayer().getInventory().getItemInMainHand().getItemMeta().displayName().toString().contains("Wand")
        ) {
            event.setCancelled(true);
            Prison.texter.response(event.getPlayer(), "&fFirst position set!");
            ConfigDb.loc1 = event.getBlock().getLocation();
            return;
        }

        if (event.getPlayer().getGameMode() != GameMode.SURVIVAL) {
            return;
        }

        Material mat = event.getBlock().getType();

        Block block = StorageManager.getBlock(mat);


        if (block == null)
            return;

        if (PlayerStorage.economy.get(event.getPlayer().getUniqueId()).getLevel() < block.getLevel()) {
            event.setCancelled(true);
            Utils.displayAction(event.getPlayer(), "§cYou need to be at least level " + block.getLevel() + " to mine this block!", 20);

            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1);
            return;
        }

        event.getPlayer().getInventory().addItem(new ItemStack(Utils.remapDrop(event.getBlock().getType()), 1));

        event.setDropItems(false);

        // Give rewards
        final double multiplier = LevelService.playerCache.getMultiplier(event.getPlayer().getUniqueId());
        final double xp = block.getXp() * multiplier;
        final double coins = block.getCoins() * multiplier;

        Utils.displayAction(event.getPlayer(), "§a+ " + xp + " XP  &r|  §6+ " + coins + " $", 60);
        PlayerStorage.add(event.getPlayer().getUniqueId(), xp, coins);


        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        BoardService.updateBoard(event.getPlayer().getUniqueId());
        LevelService.checkLevel(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {
        if (
                event.getAction() != Action.RIGHT_CLICK_BLOCK ||
                        event.getPlayer().getInventory().getItemInMainHand().getType() != Material.BLAZE_ROD ||
                        !event.getPlayer().getInventory().getItemInMainHand().getItemMeta().displayName().toString().contains("Wand")
        ) {
            return;
        }

        event.setCancelled(true);
        Prison.texter.response(event.getPlayer(), "&fSecond position set!");
        ConfigDb.loc2 = Objects.requireNonNull(event.getClickedBlock()).getLocation();
    }
}
