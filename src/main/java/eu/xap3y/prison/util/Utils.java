package eu.xap3y.prison.util;

import eu.xap3y.prison.Prison;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

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

    public static Material remapDrop(Material mat) {
        return remap.getOrDefault(mat, mat);
    }
}
