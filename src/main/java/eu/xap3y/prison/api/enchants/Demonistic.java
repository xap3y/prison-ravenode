package eu.xap3y.prison.api.enchants;

import eu.xap3y.prison.Prison;
import eu.xap3y.prison.api.enums.EnchantType;
import eu.xap3y.prison.api.interfaces.EnchantInterface;
import eu.xap3y.prison.manager.ParticleLooperManager;
import eu.xap3y.prison.storage.dto.Block;
import eu.xap3y.prison.storage.dto.Cell;
import eu.xap3y.prison.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class Demonistic implements EnchantInterface {

    @Override
    public @NotNull EnchantType getType() {
        return EnchantType.DEMON;
    }

    @Override
    public boolean start(Location loc, Player p0, Block lastBlock, Cell cell) {

        ParticleLooperManager looper = new ParticleLooperManager(p0);

        Location middle = loc.clone().add(0.5, 1, 0.5);
        double size = 5.0;

        Bukkit.getScheduler().runTaskAsynchronously(Prison.INSTANCE, () -> {
            Utils.summonCircle(loc, size, 300).forEach((location -> {
                looper.addLocation(location);
                try {
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    // IGNORE
                }
            }));
        });


        int points = 5;
        double angleBetweenPoints = 2 * Math.PI / points;

        Location[] starPoints = new Location[points];

        for (int i = 0; i < points; i++) {
            double angle = i * angleBetweenPoints - Math.PI / 2;
            double x = middle.getX() + size * Math.cos(angle);
            double z = middle.getZ() + size * Math.sin(angle);
            starPoints[i] = new Location(loc.getWorld(), x, middle.getY(), z);
        }

        int[] starConnections = { 0, 2, 4, 1, 3, 0 };
        for (int i = 0; i < starConnections.length - 1; i++) {
            Location start = starPoints[starConnections[i]];
            Location end = starPoints[starConnections[i + 1]];

            spawnParticlesAlongLine(start, end, looper);
        }

        return true;
    }

    private static void spawnParticlesAlongLine(Location start, Location end, ParticleLooperManager looper) {
        Vector direction = end.toVector().subtract(start.toVector());
        double distance = direction.length();
        direction.normalize();

        Bukkit.getScheduler().runTaskAsynchronously(Prison.INSTANCE, () -> {
            for (double i = 0; i < distance; i += 0.1) {
                Location current = start.clone().add(direction.clone().multiply(i));

                looper.addLocation(current);
                try {
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    // IGNORE
                }
            }
        });

    }

    @Override
    public String getName() {
        return "&4&lDEMONISTIC";
    }
}
