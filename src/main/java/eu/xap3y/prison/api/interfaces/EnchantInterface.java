package eu.xap3y.prison.api.interfaces;

import eu.xap3y.prison.storage.dto.Block;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface EnchantInterface {

    void start(Location loc, Player p0, Block lastBlock);

    String getName();

    // IN TICKS
    default long getCooldown() {
        return 20L; // 1 second
    }
}
