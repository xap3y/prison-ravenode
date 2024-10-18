package eu.xap3y.prison.api.enchants;

import eu.xap3y.prison.Prison;
import eu.xap3y.prison.api.enums.EnchantType;
import eu.xap3y.prison.api.interfaces.EnchantInterface;
import eu.xap3y.prison.services.BoardService;
import eu.xap3y.prison.services.BreakService;
import eu.xap3y.prison.services.LevelService;
import eu.xap3y.prison.storage.dto.Cell;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BlastBreakerEnchant implements EnchantInterface {

    @Override
    public long getCooldown() {
        return 2500L;
    }

    @Override
    public @NotNull EnchantType getType() {
        return EnchantType.BLAST_BREAKER;
    }

    @Override
    public boolean start(Location loc, Player p0, eu.xap3y.prison.storage.dto.Block lastBlock, Cell cell) {
        Bukkit.getScheduler().runTaskAsynchronously(Prison.INSTANCE, () -> {
            // BREAK 5x5 location where the middle is the location
            for (int x = loc.getBlockX() - 1; x <= loc.getBlockX() + 1; x++) {
                for (int y = loc.getBlockY() - 1; y <= loc.getBlockY() + 1; y++) {
                    for (int z = loc.getBlockZ() - 1; z <= loc.getBlockZ() + 1; z++) {
                        Block temp = loc.getWorld().getBlockAt(x, y, z);
                        if (temp.getType() == lastBlock.getMat()) {
                            BoardService.updateBoard(p0.getUniqueId());
                            LevelService.checkLevel(p0.getUniqueId());
                            BreakService.process(lastBlock, p0, false);
                            Bukkit.getScheduler().runTask(Prison.INSTANCE, () -> temp.setType(Material.AIR));
                        }
                        try {
                            Thread.sleep(40);
                        } catch (InterruptedException e) {
                            // IGNORE
                        }
                    }
                }
            }
        });

        return true;
    }
}
