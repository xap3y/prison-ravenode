package eu.xap3y.prison.storage.dto;

import lombok.Data;
import lombok.SneakyThrows;
import org.bukkit.Location;

public @Data class Cell {

    public String name;
    public Location spawn;
    public String head;
    public int minLevel;
    public Blocks blocks;
    public Location pos1;
    public Location pos2;

    private int xMin;
    private int xMax;
    private int yMin;
    private int yMax;
    private int zMin;
    private int zMax;

    public Cell(String name, Location spawn, String head, int minLevel, Blocks blocks, Location pos1, Location pos2) {
        this.name = name;
        this.spawn = spawn;
        this.head = head;
        this.minLevel = minLevel;
        this.blocks = blocks;
        this.pos1 = pos1;
        this.pos2 = pos2;
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
}
