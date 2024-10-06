package eu.xap3y.prison.api.enchants;

import eu.xap3y.prison.Prison;
import eu.xap3y.prison.api.interfaces.EnchantInterface;
import eu.xap3y.prison.manager.CooldownManager;
import eu.xap3y.prison.services.BreakService;
import eu.xap3y.prison.storage.dto.Cell;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class TestEnchant implements EnchantInterface {

    @Override
    public long getCooldown() {
        return 20L;
    }

    @Override
    public void start(Location loc, Player p0, eu.xap3y.prison.storage.dto.Block lastBlock, Cell cell) {

        CooldownManager.setCooldown(p0);

        Bukkit.getScheduler().runTaskAsynchronously(Prison.INSTANCE, () -> {
            // BREAK 5x5 location where the middle is the location
            for (int x = loc.getBlockX() - 1; x <= loc.getBlockX() + 1; x++) {
                for (int y = loc.getBlockY() - 1; y <= loc.getBlockY() + 1; y++) {
                    for (int z = loc.getBlockZ() - 1; z <= loc.getBlockZ() + 1; z++) {
                        Block temp = loc.getWorld().getBlockAt(x, y, z);
                        if (temp.getType() == lastBlock.getMat()) {
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
    }

    @Override
    public String getName() {
        return "TestEnchant";
    }
}
