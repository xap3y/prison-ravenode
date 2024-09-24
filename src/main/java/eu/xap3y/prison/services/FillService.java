package eu.xap3y.prison.services;

import eu.xap3y.prison.Prison;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

public @Data class FillService {

    // 120ms delay between each row fill
    private static final int FILL_DELAY = 120;

    private Location loc1;
    private Location loc2;
    private Material[] mats;

    public FillService(Location loc1, Location loc2, Material[] mats) {
        this.loc1 = loc1;
        this.loc2 = loc2;
        this.mats = mats;
    }

    public void start() {
        fill();
    }

    private void fill() {

        int startY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int endY = Math.max(loc1.getBlockY(), loc2.getBlockY());

        new BukkitRunnable() {
            @Override
            public void run() {
                Prison.texter.console("RUNNING FILLER");//DEBUG
                for(int y = startY; y <= endY;y++) {
                    Prison.texter.console("Filling row " + y);// DEBUG
                    fillRow(y);
                    try {
                        Thread.sleep(FILL_DELAY);
                    } catch (InterruptedException e) {
                        // IGNORE
                    }
                }
            }
        }.runTaskAsynchronously(Prison.INSTANCE);
    }

    private void fillRow(int y) {

        int startX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int endX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int startZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int endZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        // Legacy
        /*for (int x = loc1.getBlockX(); x <= loc2.getBlockX(); x++) {
            for (int z = loc1.getBlockZ(); z <= loc2.getBlockZ(); z++) {
                Material random = mats[(int) (Math.random() * mats.length)];
                loc1.getWorld().getBlockAt(x, loc2.getBlockY(), z).setType(random);
            }
        }*/

        new BukkitRunnable() {
            int currentX = startX;

            @Override
            public void run() {
                if (currentX > endX) {
                    Prison.texter.console("Cancelling filler");// DEBUG
                    cancel();
                    return;
                }
                for (int z = startZ; z <= endZ; z++) {
                    Material random = mats[(int) (Math.random() * mats.length)];
                    Prison.texter.console("Setting block at " + currentX + " " + y + " " + z + " to " + random);// DEBUG
                    loc1.getWorld().getBlockAt(currentX, y, z).setType(random);
                }
                currentX++;
            }
        }.runTaskTimer(Prison.INSTANCE, 0L, 1L);
    }
}
