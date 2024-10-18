package eu.xap3y.prison.storage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlayerDto {

    public double coins;
    public double xp;
    public int level;
    public int prestiges;
    public int blocksMined;

    public void reset() {
        this.coins = 0;
        this.xp = 0;
        this.level = 0;
        this.prestiges = 0;
        this.blocksMined = 0;
    }
}