package eu.xap3y.prison.api.enums;

import lombok.Getter;

@Getter
public enum LeaderBoardType {

    LEVEL("Level"),
    PRESTIGES("Prestiges"),
    COINS("Coins"),
    BLOCKS_MINED("Blocks Mined");

    private final String label;

    LeaderBoardType(String label) {
        this.label = label;
    }
}
