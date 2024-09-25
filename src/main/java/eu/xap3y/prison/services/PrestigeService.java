package eu.xap3y.prison.services;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PrestigeService {

    public static final double PRESTIGE_MULTIPLIER = 1.12;

    public static boolean canPrestige(UUID p0) {
        return true;
    }

    public static double getPrestigeMultiplier(int prestiges) {
        return PRESTIGE_MULTIPLIER * prestiges;
    }

    public static void prestige(Player p0) {

    }
}
