package eu.xap3y.prison.storage;

import eu.xap3y.prison.Prison;
import eu.xap3y.prison.api.enums.EnchantType;
import eu.xap3y.prison.api.enums.UpgradeSolution;
import eu.xap3y.prison.api.interfaces.Debug;
import eu.xap3y.prison.api.persistents.ToolEnchantType;
import eu.xap3y.prison.api.typealias.PairArray;
import eu.xap3y.prison.storage.dto.ToolAttributes;
import eu.xap3y.prison.storage.dto.UpgradeRes;
import eu.xap3y.xagui.models.GuiButton;
import kotlin.Pair;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;


public class ConfigDb {

    public static final String LINE = "&a&m-+-------------------------------------------+-";

    public static final String ONLY_PLAYER = "&cOnly players can use this command!";

    public static final ItemStack WAND = new GuiButton(Material.BLAZE_ROD).setName("&b&lWand").setLore("", "&7Select two points to create mine").getItem();

    public static Location loc1, loc2, spawn;

    public static int resetDelay = 6000;

    public static ToolEnchantType enchantTypeArrayDataType = new ToolEnchantType();

    public static final String TOOL_NAME_PATTERN = "&f&l%s &7&l(&b&l%s&7&l)";

    public static final PairArray fortuneMapper = new PairArray() {{
        add(new Pair<>(1.2f, 30));
        add(new Pair<>(1.6f, 40));
        add(new Pair<>(2f, 55));
        add(new Pair<>(2.3f, 60));
        add(new Pair<>(2.8f, 70));
        add(new Pair<>(3.2f, 75));
        add(new Pair<>(3.8f, 80));
        add(new Pair<>(4f, 90));
    }};

    @Nullable
    public static Player lastPlayer = null;

    public static ItemStack getDefaultPickaxe(int level) {
        return toolMapper.get(level).getTool();
    }

    @Debug
    public static ItemStack getAdvancedPickaxe(EnchantType type) {
        ItemStack item = getDefaultPickaxe(1);
        ItemMeta meta = item.getItemMeta();
        //meta.addEnchant(Enchantment.DIG_SPEED, 5, true);
        if (type != EnchantType.ALL)
            meta.getPersistentDataContainer().set(PRISON_ENCH_KEY, enchantTypeArrayDataType, new EnchantType[]{type});
        else
            meta.getPersistentDataContainer().set(PRISON_ENCH_KEY, enchantTypeArrayDataType, new EnchantType[]{EnchantType.TNT, EnchantType.BLAST_BREAKER});
        meta.getPersistentDataContainer().set(PRISON_TOOL_LEVEL_KEY, PersistentDataType.INTEGER, 1);
        item.setItemMeta(meta);

        return item;
    }

    public static final NamespacedKey PRISON_ENCH_KEY = new NamespacedKey(Prison.INSTANCE, "prison_ench");
    public static final NamespacedKey PRISON_TOOL_LEVEL_KEY = new NamespacedKey(Prison.INSTANCE, "tool_level");

    @Debug // TODO: Migrate to ToolDto
    public static final HashMap<Integer, ToolAttributes> toolMapper = new HashMap<>() {{
       put(0, new ToolAttributes("Iron Pickaxe", Material.IRON_PICKAXE, 0));
       put(1, new ToolAttributes("Iron Pickaxe", Material.IRON_PICKAXE, 1));
       put(2, new ToolAttributes("Iron Pickaxe", Material.IRON_PICKAXE, 2));
    }};

    private static final GuiButton DEFAULT_UPGRADE = new GuiButton(Material.ANVIL);
    private static final GuiButton DEFAULT_UPGRADE_ERROR = new GuiButton(Material.BARRIER).setName("&c&lError");

    public static final HashMap<UpgradeSolution, UpgradeRes> upgradeResMapper = new HashMap<>() {{
        put(UpgradeSolution.ALLOWED, new UpgradeRes(DEFAULT_UPGRADE.clone().setName("&a&lUpgrade"), true));
        put(UpgradeSolution.COMPLETED, new UpgradeRes(DEFAULT_UPGRADE.clone().setName("&a&lUpgraded").setLore("", "&eUpgrade was completed"), false));
        put(UpgradeSolution.WRONG_ITEM, new UpgradeRes(DEFAULT_UPGRADE_ERROR.clone().setLore(" ", "&fWrong tool!"), false));
        put(UpgradeSolution.INVALID_ITEM, new UpgradeRes(DEFAULT_UPGRADE_ERROR.clone().setLore(" ", "&fInvalid tool!"), false));
        put(UpgradeSolution.NO_ITEM, new UpgradeRes(DEFAULT_UPGRADE.clone().setName("&a&lUpgrade").setLore(" ", "&fInsert item"), false));
        put(UpgradeSolution.MAX_LEVEL, new UpgradeRes(DEFAULT_UPGRADE_ERROR.clone().setLore(" ", "&fThis tool is already at its max level!"), false));
        put(UpgradeSolution.UNKNOWN, new UpgradeRes(DEFAULT_UPGRADE_ERROR.clone().setLore(" ", "&fCannot upgrade this tool!"), false));

    }};
    // This is a fucking mess, instead it should be getting those values from the config.yml
    // EDIT: WHY THIS EVEN EXIST, this should be part of the Cell class
    // Also, level requirement should be bounded on the cell, not blocks D: TODO: FIX THIS MESS
    // 09/25 added % finally
    /*public static final HashMap<Material, Block> BLOCK_MAPPER = new HashMap<>() {{
        put(Material.STONE, new Block(0, 10, 10, 50, Material.STONE));       // 50%
        put(Material.COBBLESTONE, new Block(0, 10, 10, 50, Material.COBBLESTONE)); // 50%

        put(Material.BRICKS, new Block(6, 20, 20, 50, Material.BRICKS));       // 50%
        put(Material.MUD_BRICKS, new Block(6, 20, 20, 50, Material.MUD_BRICKS));   // 50%

        put(Material.DIORITE, new Block(11, 30, 30, 50, Material.DIORITE));       // 50%
        put(Material.POLISHED_DIORITE, new Block(11, 30, 30, 50, Material.POLISHED_DIORITE));   // 50%

        put(Material.ANDESITE, new Block(21, 40, 40, 50, Material.ANDESITE));       // 50%
        put(Material.POLISHED_ANDESITE, new Block(21, 40, 40, 50, Material.POLISHED_ANDESITE));   // 50%

        put(Material.SAND, new Block(31, 50, 50, 50, Material.SAND));       // 50%
        put(Material.SANDSTONE, new Block(31, 50, 50, 50, Material.SANDSTONE));   // 50%

        put(Material.TERRACOTTA, new Block(41, 60, 60, 50, Material.TERRACOTTA));       // 50%
        put(Material.PACKED_MUD, new Block(41, 60, 60, 50, Material.PACKED_MUD));   // 50%

        put(Material.GOLD_BLOCK, new Block(0, 2000, 0, 50, Material.GOLD_BLOCK));//DEBUG
    }};*/

    // HEADS BASE64
    public static final String BARRIER = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2VkMWFiYTczZjYzOWY0YmM0MmJkNDgxOTZjNzE1MTk3YmUyNzEyYzNiOTYyYzk3ZWJmOWU5ZWQ4ZWZhMDI1In19fQ==";
    public static final String BACK = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==";
    public static final String CRATES = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2UyZWI0NzUxZTNjNTBkNTBmZjE2MzUyNTc2NjYzZDhmZWRmZTNlMDRiMmYwYjhhMmFhODAzYjQxOTM2M2NhMSJ9fX0=";
    public static final String PRESTIGE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzc3ZDRhMjA2ZDc3NTdmNDc5ZjMzMmVjMWEyYmJiZWU1N2NlZjk3NTY4ZGQ4OGRmODFmNDg2NGFlZTdkM2Q5OCJ9fX0=";
}
