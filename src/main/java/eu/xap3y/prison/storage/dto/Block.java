package eu.xap3y.prison.storage.dto;

import lombok.Data;

public @Data class Block {

    private final int percentage;
    private final int xp;
    private final int coins;

    public Block(int percentage, int xp, int coins) {
        this.percentage = percentage;
        this.xp = xp;
        this.coins = coins;
    }
}
