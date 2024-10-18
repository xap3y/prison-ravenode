package eu.xap3y.prison.api.enchants;

import eu.xap3y.prison.Prison;
import eu.xap3y.prison.api.enums.EnchantType;
import eu.xap3y.prison.api.interfaces.EnchantInterface;
import eu.xap3y.prison.manager.ParticleLooperManager;
import eu.xap3y.prison.storage.dto.Block;
import eu.xap3y.prison.storage.dto.Cell;
import eu.xap3y.prison.util.Utils;
import eu.xap3y.xalib.objects.TextModifier;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.awt.geom.Point2D;
import java.util.logging.Level;

public class Demonistic implements EnchantInterface {

    @Override
    public @NotNull EnchantType getType() {
        return EnchantType.DEMON;
    }

    @Override
    public boolean start(Location loc, Player p0, Block lastBlock, Cell cell) {

        ParticleLooperManager looper = new ParticleLooperManager(p0);

        double size = 7.0;

        Location middle = loc.clone().add(-0.7, 1, size-3d);

        Bukkit.getScheduler().runTaskAsynchronously(Prison.INSTANCE, () -> {
            for (double x = 0.0; x < 1.0;x+=0.25) {
                Location temp = middle.clone().subtract(0,0.25, 0);
                drawPentagram(temp, size, looper);
                Bukkit.getScheduler().runTaskAsynchronously(Prison.INSTANCE, () -> {
                    Utils.summonCircle(loc, size-3.3, 300).forEach((location -> {
                        looper.addLocation(location);
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            // IGNORE
                        }
                    }));
                });
            }

            // break 11x11 blocks from loc variable
            for (int x = -5; x < 6; x++) {
                for (int z = -5; z < 6; z++) {
                    Location temp = loc.clone().add(x, 0, z);
                    if (temp.getBlock().getType().isSolid()) {
                        Bukkit.getScheduler().runTask(Prison.INSTANCE, () -> temp.getBlock().setType(Material.AIR));
                    }
                }
            }

            try {
                Thread.sleep(1000);
                looper.destroy();
            } catch (InterruptedException e) {
                // IGNORE
            }
        });
        /*


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
        }*/

        return true;
    }

    private static void spawnParticlesAlongLine(Location start, Location end, ParticleLooperManager looper) {
        Vector direction = end.toVector().subtract(start.toVector());
        double distance = direction.length();
        direction.normalize();

        Bukkit.getScheduler().runTaskAsynchronously(Prison.INSTANCE, () -> {
            for (double i = 0; i < distance; i += 0.1) {
                Location current = start.clone().add(direction.clone().multiply(i));

                Prison.texter.response(looper.getP0(), "Current: " + Utils.fixDecimals(current.getX()) + " | " + current.getY() + " | " + Utils.fixDecimals(current.getZ()), new TextModifier(false, true));
                Prison.texter.debugLog("Current: " + Utils.fixDecimals(current.getX()) + " | " + current.getY() + " | " + Utils.fixDecimals(current.getZ()), Level.INFO);
                looper.addLocation(current);
                try {
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    // IGNORE
                }
            }
        });
    }

    private static void drawLine(Point2D.Double startPoint, Point2D.Double endPoint, double step, ParticleLooperManager looper, Location loc) {
        double distance = startPoint.distance(endPoint);
        double steps = distance / step;
        double dx = (endPoint.x - startPoint.x) / steps;
        double dy = (endPoint.y - startPoint.y) / steps;

        Point2D.Double iterPoint = new Point2D.Double(startPoint.x, startPoint.y);

        Bukkit.getScheduler().runTaskAsynchronously(Prison.INSTANCE, () -> {
            for (int i = 0; i <= steps; i++) {
                Location location = new Location(loc.getWorld(), iterPoint.x, loc.getY(), iterPoint.y);
                try {
                    Thread.sleep(3);
                } catch (InterruptedException e) {
                    //
                }
                looper.addLocation(location);
                iterPoint.x += dx;
                iterPoint.y += dy;
            }
        });
    }

    public static void drawPentagram(Location loc, double lineLength, ParticleLooperManager looper) {

        final Point2D.Double[] currentPoint = {new Point2D.Double(loc.getX(), loc.getZ())};

        double angleBetweenPoints = 2 * Math.PI / 5;
        final double[] currentAngle = {-Math.PI / 2};
        final double loops = lineLength / 0.1;

        Bukkit.getScheduler().runTaskAsynchronously(Prison.INSTANCE, () -> {
            for (int i = 0; i < 5; i++) {
                Point2D.Double nextPoint = getNextPoint(currentPoint[0], currentAngle[0], lineLength);

                drawLine(currentPoint[0], nextPoint, 0.1, looper, loc);
                currentPoint[0] = nextPoint;
                currentAngle[0] += 2 * angleBetweenPoints;
                try {
                    Thread.sleep((long) (3d*loops));
                } catch (InterruptedException e) {
                    //
                }
            }
        });
    }

    private static Point2D.Double getNextPoint(Point2D.Double start, double angle, double length) {
        double x = start.x + length * Math.cos(angle);
        double y = start.y + length * Math.sin(angle);
        return new Point2D.Double(x, y);
    }

    @Override
    public long getCooldown() {
        return 10000L;
    }
}
