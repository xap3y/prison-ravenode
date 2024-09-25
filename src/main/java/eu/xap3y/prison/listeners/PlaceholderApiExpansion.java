package eu.xap3y.prison.listeners;

import eu.xap3y.prison.api.enums.CellType;
import eu.xap3y.prison.services.CellService;
import eu.xap3y.prison.services.LevelService;
import eu.xap3y.prison.storage.PlayerStorage;
import eu.xap3y.prison.storage.StorageManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;

public class PlaceholderApiExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "prison";
    }

    @Override
    public @NotNull String getAuthor() {
        return "XAP3Y";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) return null;

        String[] args = params.split("_");

        if (params.startsWith("cell_reset_time_")) {
            if (args.length != 4) return null;

            String prisonName = args[3];

            for (CellType type : CellType.values()) {
                if (type.name().equalsIgnoreCase(prisonName)) {
                    return CellService.cellMapper.get(type).getSecondsLeft() + "";
                }
            }
            return null;
        }

        return switch (params) {
            case "prestiges" -> PlayerStorage.economy.get(player.getUniqueId()).getPrestiges() + "";
            case "level" -> PlayerStorage.economy.get(player.getUniqueId()).getLevel() + "";
            case "coins" -> PlayerStorage.economy.get(player.getUniqueId()).getCoins() + "";
            case "xp" -> PlayerStorage.economy.get(player.getUniqueId()).getXp() + "";
            case "level_progress" -> LevelService.shortProgress(player.getUniqueId(), 12);
            default -> null;
        };
    }
}
