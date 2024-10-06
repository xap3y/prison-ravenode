package eu.xap3y.prison.api.enchants;

import eu.xap3y.prison.Prison;
import eu.xap3y.prison.api.interfaces.EnchantInterface;
import eu.xap3y.prison.manager.CooldownManager;
import eu.xap3y.prison.services.BoardService;
import eu.xap3y.prison.services.BreakService;
import eu.xap3y.prison.storage.StorageManager;
import eu.xap3y.prison.storage.dto.Block;
import eu.xap3y.prison.storage.dto.Cell;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;


public class ExplosiveEnchant implements EnchantInterface {


    // 90 degrees rotated "octahedron" shape of blocks to break
    private static final int[][] blockOffsets = {

            // FIRST Y LAYER:
            {0, 0, -1}, {0, 0, -2}, {1, 0, -1}, {1, 0, 0}, {2, 0, 0}, {1, 0, 1},
            {0, 0, 1}, {0, 0, 2}, {-1, 0, 1}, {-1, 0, 0}, {-2, 0, 0}, {-1, 0, -1},

            // Y-1 LAYER:
            {0, -1, 0}, {1, -1, 0}, {0, -1, 1}, {-1, -1, 0}, {0, -1, -1},

            // Y+1 LAYER:
            {1, 1, 0}, {0, 1, 1}, {0, 1, -1}, {-1, 1, 0}, {0, 1, 0}
    };

    @Override
    public long getCooldown() {
        return 20L; // For debugging purposes
    }

    @Override
    public void start(Location loc, Player p0, Block lastBlock, Cell cell) {

        CooldownManager.setCooldown(p0);

        //Prison.texter.response(p0, "TNT ENCHANT");

        double min = 0.0;
        double max = 2.5;
        double transform = 0.25;

        Bukkit.getScheduler().runTaskAsynchronously(Prison.INSTANCE, () -> {

            for (double i = min; i <= max; i += transform) {
                //yModifier -= (Y_MODIFIER_DEF / (max - transform * max)) * (max + transform); // Too much decrease
                summonCircle(loc, i, p0);

                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    // IGNORE
                }
            }

            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                // IGNORE
            }

            for (int[] offset : blockOffsets) {
                int xOffset = offset[0];
                int yOffset = offset[1];
                int zOffset = offset[2];


                Location blockLoc = new Location(loc.getWorld(), loc.getBlockX() + xOffset, loc.getBlockY() + yOffset, loc.getBlockZ() + zOffset);
                org.bukkit.block.Block block = blockLoc.getBlock();

                Prison.parApi.LIST_1_13.FLASH
                        .packet(true, blockLoc)
                        .sendTo(p0);

                Block tempBlock = StorageManager.getBlock(cell, block.getType());
                if (tempBlock != null) {
                    BreakService.process(tempBlock, p0, false);
                    Bukkit.getScheduler().runTask(Prison.INSTANCE, () -> block.setType(Material.AIR));
                }

                BoardService.updateBoard(p0.getUniqueId(), false);
                p0.playSound(p0, Sound.BLOCK_DECORATED_POT_INSERT_FAIL, 1f, 1f);
            }
        });

    }

    @Override
    public String getName() {
        return "";
    }


    private void summonCircle(Location location, double size, Player p0) {
        for (int d = 0; d <= 20; d += 1) {
            Location particleLoc = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
            particleLoc.setX((location.getX() + Math.cos(d) * size)+0.5);
            particleLoc.setY(location.getY()+1.0);
            particleLoc.setZ((location.getZ() + Math.sin(d) * size)+0.5);

            if (Prison.DEBUG) {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    Prison.texter.response(player, "CIR-X: &c" + particleLoc.getBlockX());
                    Prison.texter.response(player, "CIR-Y: &c" + particleLoc.getBlockY());
                    Prison.texter.response(player, "CIR-Z: &c" + particleLoc.getBlockZ());
                });
            }

            Prison.parApi.LIST_1_13.ASH
                    .packet(true, particleLoc)
                    .sendTo(p0);

            Prison.parApi.LIST_1_13.DUST_PLUME
                    .packet(true, particleLoc)
                    .sendTo(p0);

            Prison.parApi.LIST_1_8.REDSTONE
                    .packet(true, particleLoc)
                    .sendTo(p0);
        }
    }
}
