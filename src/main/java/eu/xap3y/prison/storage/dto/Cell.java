package eu.xap3y.prison.storage.dto;

import eu.xap3y.prison.Prison;
import eu.xap3y.prison.api.enums.CellType;
import eu.xap3y.prison.services.CellService;
import eu.xap3y.prison.storage.ConfigDb;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

public @Data class Cell {

    public String name;
    public Location spawn;
    public String head;
    public int minLevel;
    public Blocks blocks;
    public Location pos1;
    public Location pos2;
    public CellType type;
    public Block[] blockArray;

    private int xMin;
    private int xMax;
    private int yMin;
    private int yMax;
    private int zMin;
    private int zMax;

    public LocalDateTime lastReset;
    public final long RESET_DELAY = 20L; // 1 second
    private final long CHECK_DELAY = 60 * 5; // 5 minutes
    public int resetTaskId;

    public int secondsLeft;


    public Cell(CellType type, String name, Location spawn, String head, int minLevel, Blocks blocks, Location pos1, Location pos2, Block[] blockArr) {
        this.type = type;
        this.name = name;
        this.spawn = spawn;
        this.head = head;
        this.minLevel = minLevel;
        this.blocks = blocks;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.blockArray = blockArr;
        this.secondsLeft = ConfigDb.resetDelay;

        lastReset = LocalDateTime.now();
    }

    public void refreshLocs() {
        if (pos1 == null || pos2 == null) {
            return;
        }
        this.xMin = Math.min(pos1.getBlockX(), pos2.getBlockX());
        this.xMax = Math.max(pos1.getBlockX(), pos2.getBlockX());
        this.yMin = Math.min(pos1.getBlockY(), pos2.getBlockY());
        this.yMax = Math.max(pos1.getBlockY(), pos2.getBlockY());
        this.zMin = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        this.zMax = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
    }

    // This will calculate if the player or block is inside the 3D area of the cell
    public boolean isInside(Location loc) {
        return loc.getX() >= xMin && loc.getX() <= xMax &&
                loc.getY() >= yMin && loc.getY() <= yMax &&
                loc.getZ() >= zMin && loc.getZ() <= zMax;
    }

    public void registerResetBukkitTask() {

        AtomicInteger current = new AtomicInteger();
        AtomicInteger current2 = new AtomicInteger();

        this.resetTaskId = Bukkit.getScheduler().runTaskTimerAsynchronously(Prison.INSTANCE, () -> {
            if (current2.get() >= CHECK_DELAY) {
                current2.set(0);
                if (this.isEmpty()) {
                    CellService.resetCell(this.type);
                    secondsLeft = ConfigDb.resetDelay;
                    return;
                }
            }
            current2.getAndIncrement();
            if (current.get() >= ConfigDb.resetDelay) {
                current.set(0);
                CellService.resetCell(this.type);
                secondsLeft = ConfigDb.resetDelay;
            } else {
                secondsLeft--;
                current.getAndIncrement();
            }
        }, this.RESET_DELAY, this.RESET_DELAY).getTaskId();
    }

    public boolean isEmpty() {
        int startY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int endY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int startX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int endX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int startZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int endZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
        World temp = pos1.getWorld();
        for (int x=startX;x<=endX;x++) {
            for (int z=startZ;z<=endZ;z++) {
                for (int y=startY;y<=endY;y++) {
                    if (temp.getBlockAt(x, y, z).getType() != Material.AIR)
                        return false;
                }
            }
        }
        return true;
    }
}
