package eu.xap3y.prison.util;

import eu.xap3y.prison.Prison;
import eu.xap3y.prison.api.enums.EnchantType;
import eu.xap3y.prison.api.enums.LeaderBoardType;
import eu.xap3y.prison.storage.ConfigDb;
import eu.xap3y.prison.storage.PlayerStorage;
import eu.xap3y.prison.storage.dto.ToolDto;
import eu.xap3y.xagui.models.GuiButton;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.*;
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

    public static @Nullable ToolDto deserializeTool(ItemStack item) {
        EnchantType[] enchants = item.getItemMeta().getPersistentDataContainer().get(ConfigDb.PRISON_ENCH_KEY, ConfigDb.enchantTypeArrayDataType);
        if (enchants == null) {
            return null;
        }

        Integer level = item.getItemMeta().getPersistentDataContainer().get(ConfigDb.PRISON_TOOL_LEVEL_KEY, PersistentDataType.INTEGER);

        if (level == null) {
            return null;
        }

        String name = item.getItemMeta().getDisplayName();

        return new ToolDto(item.getType(), name, level, enchants, item.getItemMeta().getEnchants());
    }

    public static ItemStack constructTool(ToolDto tool) {
        ItemStack item = new GuiButton(tool.getMaterial())
                .setName(tool.getName())
                .getItem();

        ArrayList<Component> loreBuilder = new ArrayList<>() {{
            add(Component.empty());
            add(Component.text("§7Level: §b" + tool.getLevel()));
        }};

        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(ConfigDb.PRISON_TOOL_LEVEL_KEY, PersistentDataType.INTEGER, tool.getLevel());
        meta.setAttributeModifiers(tool.getMaterial().getDefaultAttributeModifiers(tool.getMaterial().getEquipmentSlot()));

        if (tool.enchantments != null && !tool.enchantments.isEmpty()) {
            loreBuilder.add(Component.empty());
            loreBuilder.add(Component.text("§fEnchantments:"));
            tool.enchantments.forEach((e, lvl) -> {
                loreBuilder.add(Component.text(" §7• §b" + e.getKey().getKey() + " §9" + intToRoman(lvl)));
                meta.addEnchant(e, lvl, true);
            });
        }

        if (tool.getEnchants() != null) {
            meta.getPersistentDataContainer().set(ConfigDb.PRISON_ENCH_KEY, ConfigDb.enchantTypeArrayDataType, tool.getEnchants());

            loreBuilder.add(Component.empty());
            loreBuilder.add(Component.text("§7Special Enchants:"));
            for (EnchantType enchant : tool.getEnchants()) {
                if (enchant == null) continue;
                loreBuilder.add(Component.text(" §7• §b" + enchant.getLabel()));
            }
        }

        meta.setUnbreakable(true);
        meta.addItemFlags(
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_ITEM_SPECIFICS,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_DESTROYS
        );


        meta.lore(loreBuilder);
        item.setItemMeta(meta);
        return item;
    }

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

    public static String intToRoman(int num) {
        int[] n = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] s = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        int i = 0;
        StringBuilder str = new StringBuilder();
        while (num>0){
            if (num>=n[i]){
                str.append(s[i]);
                num-=n[i];
            } else{
                i++;
            }
        }
        return str.toString();
    }
}
