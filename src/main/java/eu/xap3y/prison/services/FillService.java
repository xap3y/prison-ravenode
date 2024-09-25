package eu.xap3y.prison.services;

import eu.xap3y.prison.Prison;
import eu.xap3y.prison.storage.dto.Block;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public @Data class FillService {

    // 120ms delay between each row fill
    private static final int FILL_DELAY = 120;

    private Location loc1;
    private Location loc2;
    private Random randomNum;
    private Block[] blocks;

    public FillService(Location loc1, Location loc2, Block[] blockArray) {
        this.loc1 = loc1;
        this.loc2 = loc2;
        this.blocks = blockArray;

        randomNum = new Random();
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
                if (Prison.DEBUG) Prison.texter.console("RUNNING FILLER");//DEBUG
                for(int y = startY; y <= endY;y++) {
                    if (Prison.DEBUG) Prison.texter.console("Filling row " + y);// DEBUG
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

    // 20 blocks/s fill rate currently
    // TODO: make it 40 blocks/s but im already at lowest tickrate
    private void fillRow(int y) {
        final long PERIOD = 1L;
        int startX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int endX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int startZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int endZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        new BukkitRunnable() {
            int currentX = startX;
            @Override
            public void run() {
                if (currentX > endX) {
                    if (Prison.DEBUG) Prison.texter.console("CANCEL, NEXT ROW");// DEBUG
                    cancel();
                    return;
                }

                for (int z = startZ; z <= endZ; z++) {
                    Material mat = null;
                    do {

                        // This is fucking mess and it doesn't even work
                        /*int i = 0;
                        for (Block block : blocks) {
                            i++;
                            if (Prison.DEBUG) Prison.texter.console("&aLOOPING BLOCK: " + block.getMat() + " &7HAVE: " + block.getPercent());// DEBUG
                            int random = randomNum.nextInt(100);
                            if (Prison.DEBUG) Prison.texter.console("&aRANDOM IS: &e" + random);// DEBUG
                            if (random <= block.getPercent()) {
                                if (Prison.DEBUG) Prison.texter.console("&aSELECTED");// DEBUG
                                mat = block.getMat();
                                break;
                            } else if (i==1 && blocks.length-1 >= i && random > blocks[i].getPercent()) {
                                if (Prison.DEBUG) Prison.texter.console("&aSECOND PASSED RAND");// DEBUG
                                mat = blocks[i].getMat();
                                break;
                            }
                        }*/

                        // Temporary fix TODO
                        int random = randomNum.nextInt(100);
                        for (Block block : blocks) {
                            if (random <= block.getPercent()) {
                                mat = block.getMat();
                                break;
                            }
                        }
                    } while (mat == null);


                    if (Prison.DEBUG) Prison.texter.console("Setting block at " + currentX + " " + y + " " + z + " to " + mat);// DEBUG
                    loc1.getWorld().getBlockAt(currentX, y, z).setType(mat);
                }
                currentX++;
            }
        }.runTaskTimer(Prison.INSTANCE, 0L, PERIOD);
    }
}
