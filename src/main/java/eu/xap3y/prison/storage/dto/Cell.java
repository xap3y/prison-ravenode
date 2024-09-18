package eu.xap3y.prison.storage.dto;

import lombok.Data;
import org.bukkit.Location;

public @Data class Cell{

    public String name;
    public Location spawn;
    public String head;
    public int minLevel;
    public Blocks blocks;
    public Location pos1;
    public Location pos2;

    public Cell(String name, Location spawn, String head, int minLevel, Blocks blocks, Location pos1, Location pos2) {
        this.name = name;
        this.spawn = spawn;
        this.head = head;
        this.minLevel = minLevel;
        this.blocks = blocks;
        this.pos1 = pos1;
        this.pos2 = pos2;
    }
}
