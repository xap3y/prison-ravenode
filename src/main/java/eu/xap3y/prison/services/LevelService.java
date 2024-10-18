package eu.xap3y.prison.services;

import eu.xap3y.prison.Prison;
import eu.xap3y.prison.api.typealias.PlayerCache;
import eu.xap3y.prison.storage.ConfigDb;
import eu.xap3y.prison.storage.PlayerStorage;
import eu.xap3y.prison.util.Utils;
import eu.xap3y.xalib.managers.Texter;
import eu.xap3y.xalib.objects.TextModifier;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class LevelService {

    // Level, XP | money multiplier
    public static final HashMap<Integer, Float> levelMapper = new HashMap<>() {{
        put(1, 1.0f);
        put(5, 1.2f);
        put(10, 1.3f);
        put(15, 1.4f);
        put(20, 1.5f);
        put(25, 1.6f);
        put(30, 1.7f);
        put(40, 1.8f);
        put(50, 1.9f);
        put(60, 2.0f);
        put(70, 2.1f);
        put(80, 2.2f);
        put(90, 2.3f);
    }};

    // XP | multiplier
    public static PlayerCache playerCache = new PlayerCache();

    public static final double STARTING_XP = 80d;

    private static float getClosesMultiplier(int level) {
        int closestLevel = 1;
        for (int i : levelMapper.keySet()) {
            if (i <= level) {
                closestLevel = i;
            }
        }
        return levelMapper.get(closestLevel);
    }

    public static double requiredXp(int level) {
        if (level < 1) {
            return STARTING_XP;
        }

        double closestLevel = getClosesMultiplier(level-1);

        return STARTING_XP + ((40 * level) * closestLevel);
    }

    public static void checkLevel(UUID id) {
        double xp = PlayerStorage.economy.get(id).getXp();
        int level = PlayerStorage.economy.get(id).getLevel()+1;

        double requiredXp = playerCache.getRequiredXp(id);
        // Check if player has enough xp to level up
        if (xp >= requiredXp) {
            playerCache.setMultiplier(id, getMultiplier(level));
            playerCache.setRequiredXp(id, requiredXp(level));
            PlayerStorage.economy.get(id).setLevel(level);
            PlayerStorage.economy.get(id).setXp(xp - requiredXp);

            // Level up thing
            levelUp(id);
        }
    }

    public static String shortProgress(UUID p0, int maxLength) {
        return shortProgress(p0, maxLength, '§');
    }

    public static String shortProgress(UUID p0, int maxLength, Character col) {
        int repeater = (int) ((PlayerStorage.economy.get(p0).getXp() / playerCache.get(p0).getLeft()) * maxLength);
        if (repeater < 0) repeater = 0;
        if (repeater > maxLength) repeater = maxLength;
        String fillText = "■".repeat(repeater);
        String emptyText = /*"□"*/ "■".repeat(maxLength - fillText.length());
        return col + "a" + fillText + col + "f" + emptyText;
    }

    public static String shortProgress(UUID p0) {
        return shortProgress(p0, 10);
    }

    @SuppressWarnings("deprecation")
    public static void levelUp(UUID id) {
        Player p0 = Bukkit.getPlayer(id);
        assert p0 != null;
        p0.playSound(p0.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        int level = PlayerStorage.economy.get(id).getLevel();
        p0.sendTitle(Texter.colored("&aLVL UP!"), Texter.colored("&c" + (level-1) + " &f&l➼ &a" + level), 10, 30, 15);
        Prison.texter.response(p0, ConfigDb.LINE, new TextModifier(false, true));
        Prison.texter.response(p0, Texter.centered("&e&lLEVEL UP!"), new TextModifier(false, true));
        Prison.texter.response(p0, Texter.centered("&aYou are now level &b&l" + level), new TextModifier(false, true));
        Prison.texter.response(p0, ConfigDb.LINE, new TextModifier(false, true));
        BoardService.updateBoard(id, true);
        p0.setLevel(level);

        checkForNewMines(p0);

        int temp = Bukkit.getScheduler().runTaskTimerAsynchronously(Prison.INSTANCE, () -> {

            if (p0.getExp() == 0.0f)
                p0.setExp(1.0f);
            else
                p0.setExp(0.0f);
        }, 0L, 5L).getTaskId();

        Bukkit.getScheduler().runTaskLaterAsynchronously(Prison.INSTANCE, () -> {
            BoardService.updateBoard(id);
            p0.setExp(0.0f);
            Bukkit.getScheduler().cancelTask(temp);
        }, 40L);
    }

    private static void checkForNewMines(Player p0) {
        CellService.cellMapper.values().forEach(cell -> {
            if (cell.getMinLevel() == PlayerStorage.economy.get(p0.getUniqueId()).getLevel()) {
                p0.sendMessage(Texter.colored("&e&l&kLL &r&a&lNEW MINE UNLOCKED! &e&l&kLL "));
                p0.sendMessage(Texter.colored("&eYou have unlocked the &b" + cell.getName() + " &emine!"));
            }
        });
    }

    public static float getMultiplier(UUID id) {
        int level = PlayerStorage.economy.get(id).getLevel();
        return getClosesMultiplier(level);
    }

    public static float getMultiplier(int level) {
        return getClosesMultiplier(level);
    }

    public static void fixExp(UUID p0) {
        double exp = PlayerStorage.economy.get(p0).getXp();

        // Fix 77.65000000000006 to 77.65
        exp = Utils.fixDecimals(exp);
        PlayerStorage.economy.get(p0).setXp(exp);
    }
}
