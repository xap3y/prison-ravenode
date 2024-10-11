package eu.xap3y.prison.listeners;

import eu.xap3y.prison.Prison;
import eu.xap3y.prison.api.enums.EnchantType;
import eu.xap3y.prison.api.interfaces.EnchantInterface;
import eu.xap3y.prison.manager.CooldownManager;
import eu.xap3y.prison.manager.EnchantManager;
import eu.xap3y.prison.services.BoardService;
import eu.xap3y.prison.services.BreakService;
import eu.xap3y.prison.services.CellService;
import eu.xap3y.prison.services.LevelService;
import eu.xap3y.prison.storage.ConfigDb;
import eu.xap3y.prison.storage.PlayerStorage;
import eu.xap3y.prison.storage.StorageManager;
import eu.xap3y.prison.storage.dto.Block;
import eu.xap3y.prison.storage.dto.Cell;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

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

        Material mat = event.getBlock().getType();

        Cell cell = CellService.cellMapper.get(StorageManager.getType(mat));

        if (cell == null)
            return;

        Block block = StorageManager.getBlock(cell, mat);

        if (block == null)
            return;

        boolean isInsideCell = cell.isInside(event.getBlock().getLocation());
        if (!isInsideCell) return;

        if (event.getPlayer().getGameMode() != GameMode.SURVIVAL && !event.getPlayer().isSneaking()) {
            event.setCancelled(true);
            Prison.texter.response(event.getPlayer(), "&cIf you want to break blocks in creative mode, please sneak!");
            return;
        } else if (event.getPlayer().getGameMode() != GameMode.SURVIVAL && event.getPlayer().isSneaking()) {
            event.setCancelled(false);
            return;
        }

        if (PlayerStorage.economy.get(event.getPlayer().getUniqueId()).getLevel() < block.getLevel()) {
            event.setCancelled(true);
            Utils.displayAction(event.getPlayer(), "§cYou need to be at least level " + block.getLevel() + " to mine this block!", 20);

            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1);
            return;
        }

        boolean callback = false;
        EnchantType[] enchants = event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().get(ConfigDb.PRISON_ENCH_KEY, ConfigDb.enchantTypeArrayDataType);
        if (enchants != null) {
            for (EnchantType type : enchants) {
                EnchantInterface enchant = EnchantManager.getEnchant(type);
                if (enchant != null) {
                    //Prison.texter.response(event.getPlayer(), "FOUND ENCHANT: " + enchant.getName());
                    if (CooldownManager.hasCooldown(event.getPlayer(), enchant.getCooldown())) {
                        break;
                    }
                    boolean succeed = enchant.start(event.getBlock().getLocation(), event.getPlayer(), block, cell);
                    if (succeed && enchant.useCallback()) {
                        callback = true;
                    }
                }
            }
        }

        Integer level =  event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().get(ConfigDb.PRISON_TOOL_LEVEL_KEY, PersistentDataType.INTEGER);
        if (level != null) {
            //Prison.texter.response(event.getPlayer(), "THIS TOOL IS LVL: " + level);
        }
        event.setDropItems(false);

        if (callback){
            return;
        }

        BreakService.process(block, event.getPlayer());
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
