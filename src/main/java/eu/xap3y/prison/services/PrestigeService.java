package eu.xap3y.prison.services;

import eu.xap3y.prison.Prison;
import eu.xap3y.prison.storage.ConfigDb;
import eu.xap3y.prison.storage.PlayerStorage;
import eu.xap3y.prison.storage.dto.PlayerDto;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PrestigeService {

    public static final double PRESTIGE_MULTIPLIER = 1.12;

    public static boolean canPrestige(UUID p0) {
        return PlayerStorage.economy.get(p0).getLevel() >= 99;
    }

    public static double getPrestigeMultiplier(int prestiges) {
        return PRESTIGE_MULTIPLIER * prestiges;
    }

    public static void prestige(Player p0) {

        if (!canPrestige(p0.getUniqueId())) {
            p0.sendMessage("§cYou cannot prestige yet!");
            return;
        }
        PlayerDto playerDto = PlayerStorage.economy.get(p0.getUniqueId());
        playerDto.setXp(0);
        playerDto.setLevel(0);
        playerDto.setCoins(0);
        int prestiges = playerDto.getPrestiges();
        playerDto.setPrestiges(prestiges + 1);
        PlayerStorage.savePlayers();
        p0.playSound(p0, Sound.BLOCK_END_PORTAL_FRAME_FILL, 1f, 1f);
        p0.sendMessage("§aYou have prestiged! You are now prestige " + (prestiges + 1) + "!");
        BoardService.updateBoard(p0.getUniqueId(), false);

        Bukkit.getScheduler().runTask(Prison.INSTANCE, () -> p0.teleport(ConfigDb.spawn));
    }
}
