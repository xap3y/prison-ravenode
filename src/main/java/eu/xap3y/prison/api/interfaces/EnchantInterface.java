package eu.xap3y.prison.api.interfaces;

import eu.xap3y.prison.api.enums.EnchantType;
import eu.xap3y.prison.storage.dto.Block;
import eu.xap3y.prison.storage.dto.Cell;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface EnchantInterface {

    //void start(Location loc, Player p0, Block lastBlock);

    @NotNull
    EnchantType getType();

    boolean start(Location loc, Player p0, Block lastBlock, Cell cell);


    default String getName() {
        return getType().getLabel();
    }

    // IN TICKS
    default long getCooldown() {
        return 20L; // 1 second
    }

    default boolean useCallback() {
        return false;
    }

    default String getDescription() {
        return "";
    }
}
