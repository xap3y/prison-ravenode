package eu.xap3y.prison.manager;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    private static final Map<UUID, Long> playerCooldowns = new HashMap<>();

    public static boolean hasCooldown(Player player, long cooldownTime) {
        UUID playerId = player.getUniqueId();
        if (!playerCooldowns.containsKey(playerId)) return false;

        long lastUsed = playerCooldowns.get(playerId);
        long currentTime = System.currentTimeMillis();

        boolean hasCooldown = (currentTime - lastUsed) < cooldownTime;
        if (!hasCooldown) playerCooldowns.remove(playerId);
        return hasCooldown;
    }

    public static void setCooldown(Player player) {
        UUID playerId = player.getUniqueId();
        playerCooldowns.put(playerId, System.currentTimeMillis());
    }
}
