package eu.xap3y.prison.storage.dto;

import lombok.Data;

public @Data class Block {

    private final int level;
    private final int xp;
    private final int coins;

    public Block(int level, int xp, int coins) {
        this.level = level;
        this.xp = xp;
        this.coins = coins;
    }
}
