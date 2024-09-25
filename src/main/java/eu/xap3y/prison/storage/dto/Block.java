package eu.xap3y.prison.storage.dto;

import lombok.Data;
import org.bukkit.Material;

public @Data class Block {

    private final int level;
    private final double xp;
    private final int coins;
    private final int percent;
    private final Material mat;

    public Block(int level, double xp, int coins, int percent, Material mat) {
        this.level = level;
        this.xp = xp;
        this.coins = coins;
        this.percent = percent;
        this.mat = mat;
    }


}
