package eu.xap3y.prison.storage.dto;

import eu.xap3y.prison.api.enums.CellType;
import lombok.Data;
import org.bukkit.Material;

import java.util.HashMap;

public @Data class Blocks {
    private final HashMap<CellType, Material[]> blocks;

    public Blocks(HashMap<CellType, Material[]> blocks) {
        this.blocks = blocks;
    }
}
