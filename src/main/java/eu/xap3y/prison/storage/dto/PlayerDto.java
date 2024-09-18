package eu.xap3y.prison.storage.dto;

import lombok.Data;

public @Data class PlayerDto {

    public double coins;
    public double xp;
    public int level;
    public int prestiges;

    public PlayerDto(double coins, double xp, int level, int prestiges) {
        this.coins = coins;
        this.xp = xp;
        this.level = level;
        this.prestiges = prestiges;
    }
}