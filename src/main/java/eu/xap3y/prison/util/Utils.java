package eu.xap3y.prison.util;

import eu.xap3y.prison.Prison;
import eu.xap3y.prison.api.enums.LeaderBoardType;
import eu.xap3y.prison.storage.PlayerStorage;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Utils {

    private static final HashMap<UUID, Integer> cooldowns = new HashMap<>();

    private static final HashMap<Material, Material> remap = new HashMap<>() {{
        put(Material.STONE, Material.COBBLESTONE);
        put(Material.BRICKS, Material.BRICK);
        put(Material.MUD_BRICKS, Material.BRICK);
        put(Material.POLISHED_DIORITE, Material.DIORITE);
        put(Material.POLISHED_ANDESITE, Material.ANDESITE);
        put(Material.TERRACOTTA, Material.DIRT);
        put(Material.SANDSTONE, Material.SAND);
        put(Material.COAL_ORE, Material.COAL);
        put(Material.COAL_BLOCK, Material.COAL);
        put(Material.IRON_ORE, Material.IRON_INGOT);
        put(Material.IRON_BLOCK, Material.IRON_INGOT);
        put(Material.GOLD_ORE, Material.GOLD_INGOT);
        put(Material.GOLD_BLOCK, Material.GOLD_INGOT);
        put(Material.COPPER_ORE, Material.COPPER_INGOT);
        put(Material.COPPER_BLOCK, Material.COPPER_INGOT);
        put(Material.DIAMOND_ORE, Material.DIAMOND);
        put(Material.DIAMOND_BLOCK, Material.DIAMOND);
        put(Material.EMERALD_ORE, Material.EMERALD);
        put(Material.EMERALD_BLOCK, Material.EMERALD);
        put(Material.LAPIS_ORE, Material.LAPIS_LAZULI);
        put(Material.LAPIS_BLOCK, Material.LAPIS_LAZULI);
        put(Material.REDSTONE_ORE, Material.REDSTONE);
        put(Material.REDSTONE_BLOCK, Material.REDSTONE);
        put(Material.ANCIENT_DEBRIS, Material.NETHERITE_SCRAP);
        put(Material.NETHERITE_BLOCK, Material.ANCIENT_DEBRIS);
    }};



    public static String getMcVersion() {
        String version = "";
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        } catch (ArrayIndexOutOfBoundsException e) {
            // 1.20.6+
        }
        return version;
    }

    public static boolean isPaper() {
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static void displayAction(Player p, String text, int duration) {
        if (cooldowns.containsKey(p.getUniqueId())) {
            Bukkit.getScheduler().cancelTask(cooldowns.get(p.getUniqueId()));
            cooldowns.remove(p.getUniqueId());
        }
        p.sendActionBar(Component.text(text.replaceAll("&","§")));
        int taskId = Bukkit.getScheduler().runTaskLater(Prison.INSTANCE, () -> p.sendActionBar(Component.empty()), duration).getTaskId();
        cooldowns.put(p.getUniqueId(), taskId);
    }

    public static double fixDecimals(double val) {
        String[] split = String.valueOf(val).split("\\.");
        if (split.length > 1) {
            if (split[1].length() > 2) {
                return Double.parseDouble(split[0] + "." + split[1].substring(0, 2));
            }
        }
        return val;
    }

    public static LinkedHashMap<UUID, Integer> getLeaderboard(LeaderBoardType type) {

        return switch (type) {
            case COINS -> {
                yield PlayerStorage.economy.entrySet().stream()
                        .sorted((e1, e2) -> Double.compare(e2.getValue().getCoins(), e1.getValue().getCoins()))
                        .collect(LinkedHashMap::new, (m, e) -> m.put(e.getKey(), (int) e.getValue().getCoins()), Map::putAll);
            }
            case LEVEL -> {
                yield PlayerStorage.economy.entrySet().stream()
                        .sorted((e1, e2) -> Integer.compare(e2.getValue().getLevel(), e1.getValue().getLevel()))
                        .collect(LinkedHashMap::new, (m, e) -> m.put(e.getKey(), e.getValue().getLevel()), Map::putAll);
            }
            case PRESTIGES -> {
                yield PlayerStorage.economy.entrySet().stream()
                        .sorted((e1, e2) -> Integer.compare(e2.getValue().getPrestiges(), e1.getValue().getPrestiges()))
                        .collect(LinkedHashMap::new, (m, e) -> m.put(e.getKey(), e.getValue().getPrestiges()), Map::putAll);
            }
            case BLOCKS_MINED -> {
                yield PlayerStorage.economy.entrySet().stream()
                        .sorted((e1, e2) -> Integer.compare(e2.getValue().getBlocksMined(), e1.getValue().getBlocksMined()))
                        .collect(LinkedHashMap::new, (m, e) -> m.put(e.getKey(), e.getValue().getBlocksMined()), Map::putAll);
            }
        };
    }


    public static LeaderBoardType getNextLeaderBoardType(LeaderBoardType type) {
        return type.ordinal() + 1 < LeaderBoardType.values().length ? LeaderBoardType.values()[type.ordinal() + 1] : LeaderBoardType.values()[0];
    }

    public static Material remapDrop(Material mat) {
        return remap.getOrDefault(mat, mat);
    }

    public static Stream<Location> summonCircle(Location location, double size, int points) {

        return IntStream.rangeClosed(0, points).mapToObj(d -> {
            Location particleLoc = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
            particleLoc.setX((location.getX() + Math.cos(d) * size) + 0.5);
            particleLoc.setY(location.getY() + 1.0);
            particleLoc.setZ((location.getZ() + Math.sin(d) * size) + 0.5);
            return particleLoc;
        });
    }
}
