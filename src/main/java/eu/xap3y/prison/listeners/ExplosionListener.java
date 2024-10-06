package eu.xap3y.prison.listeners;

import eu.xap3y.prison.Prison;
import eu.xap3y.prison.services.BreakService;
import eu.xap3y.prison.services.CellService;
import eu.xap3y.prison.storage.ConfigDb;
import eu.xap3y.prison.storage.StorageManager;
import eu.xap3y.prison.storage.dto.Cell;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.List;

public class ExplosionListener implements Listener {

    @EventHandler
    public void onExplosion(BlockExplodeEvent event) {

        List<Block> blocks = event.blockList();

        if (ConfigDb.lastPlayer == null)
            return;

        for (Block block : blocks) {

            if (Prison.DEBUG) {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    Prison.texter.response(player, "Block: " + block.getType());
                    Prison.texter.response(player, "LOC: " + block.getLocation());
                });
            }

            Cell cell = CellService.cellMapper.get(StorageManager.getType(block.getType()));

            if (cell == null)
                return;

            eu.xap3y.prison.storage.dto.Block blockDto = StorageManager.getBlock(cell, block.getType());

            if (blockDto == null) {
                event.setCancelled(true);
                return;
            }

            block.setType(Material.AIR);
            BreakService.process(blockDto, ConfigDb.lastPlayer);
        }

        ConfigDb.lastPlayer = null;
        event.setCancelled(true);

    }
}
