package eu.xap3y.prison.api.enchants;

import eu.xap3y.prison.Prison;
import eu.xap3y.prison.api.enums.EnchantType;
import eu.xap3y.prison.api.interfaces.EnchantInterface;
import eu.xap3y.prison.services.BoardService;
import eu.xap3y.prison.services.BreakService;
import eu.xap3y.prison.services.LevelService;
import eu.xap3y.prison.storage.dto.Block;
import eu.xap3y.prison.storage.dto.Cell;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class EnhancerEnchant implements EnchantInterface {

    @Override
    public @NotNull EnchantType getType() {
        return EnchantType.ENHANCER;
    }

    private Player player;

    @Override
    public boolean start(Location loc, Player p0, Block lastBlock, Cell cell) {

        // 75% chance to return
        if (Math.random() < 0.75) {
            return false;
        }

        p0.playSound(p0, Sound.BLOCK_PISTON_EXTEND, .5f, 1f);
        player=p0;
        // Loop thru every 0.15 coordinate of each block edge and display particle

        Bukkit.getScheduler().runTaskAsynchronously(Prison.INSTANCE, () -> loopBlockEdges(loc.toBlockLocation(), 0.15));

        BreakService.setNextMultiplierAdder(4.0);
        BreakService.process(lastBlock, p0);
        BoardService.updateBoard(p0.getUniqueId());
        LevelService.checkLevel(p0.getUniqueId());
        return true;
    }

    public void loopBlockEdges(Location blockLocation, double increment) {

        World world = blockLocation.getWorld();

        // Minimum and maximum coordinates of the block (it's a 1x1x1 cube in Minecraft)
        double minX = blockLocation.getX();
        double minY = blockLocation.getY();
        double minZ = blockLocation.getZ();
        double maxX = minX + 1;
        double maxY = minY + 1;
        double maxZ = minZ + 1;

        // 4 edges along X-axis (at Y = minY and Y = maxY, Z = minZ and Z = maxZ)
        loopLine(world, minX, minY, minZ, maxX, minY, minZ, increment); // bottom front edge
        loopLine(world, minX, minY, maxZ, maxX, minY, maxZ, increment); // bottom back edge
        loopLine(world, minX, maxY, minZ, maxX, maxY, minZ, increment); // top front edge
        loopLine(world, minX, maxY, maxZ, maxX, maxY, maxZ, increment); // top back edge

        // 4 edges along Y-axis (at X = minX and X = maxX, Z = minZ and Z = maxZ)
        loopLine(world, minX, minY, minZ, minX, maxY, minZ, increment); // left front vertical edge
        loopLine(world, maxX, minY, minZ, maxX, maxY, minZ, increment); // right front vertical edge
        loopLine(world, minX, minY, maxZ, minX, maxY, maxZ, increment); // left back vertical edge
        loopLine(world, maxX, minY, maxZ, maxX, maxY, maxZ, increment); // right back vertical edge

        // 4 edges along Z-axis (at Y = minY and Y = maxY, X = minX and X = maxX)
        loopLine(world, minX, minY, minZ, minX, minY, maxZ, increment); // bottom left edge
        loopLine(world, maxX, minY, minZ, maxX, minY, maxZ, increment); // bottom right edge
        loopLine(world, minX, maxY, minZ, minX, maxY, maxZ, increment); // top left edge
        loopLine(world, maxX, maxY, minZ, maxX, maxY, maxZ, increment); // top right edge
    }

    public void loopLine(World world, double x1, double y1, double z1, double x2, double y2, double z2, double increment) {
        double distanceX = x2 - x1;
        double distanceY = y2 - y1;
        double distanceZ = z2 - z1;
        double maxDistance = Math.max(Math.abs(distanceX), Math.abs(distanceY));
        maxDistance = Math.max(maxDistance, Math.abs(distanceZ));

        int steps = (int) (maxDistance / increment);

        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;
            double x = x1 + t * distanceX;
            double y = y1 + t * distanceY;
            double z = z1 + t * distanceZ;

            Location loc = new Location(world, x, y, z);
            Prison.parApi.LIST_1_13.DUST.color(Color.GRAY, 1f)
                    .packet(true, loc)
                    .sendTo(player);
        }
    }

    @Override
    public String getName() {
        return "&aEnhancer";
    }

    @Override
    public long getCooldown() {
        return 0L;
    }

    @Override
    public boolean useCallback() {
        return true;
    }
}
