package eu.xap3y.prison.storage.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.UUID;

public @Data class PlayersJson {

    private final HashMap<UUID, PlayerDto> economy;

    public PlayersJson(HashMap<UUID, PlayerDto> economy) {
        this.economy = economy;
    }
}
