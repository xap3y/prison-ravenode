package eu.xap3y.prison.storage;

import eu.xap3y.prison.Prison;
import eu.xap3y.prison.api.interfaces.EnchantInterface;
import eu.xap3y.prison.storage.dto.Block;
import eu.xap3y.xagui.models.GuiButton;
import eu.xap3y.xalib.managers.Texter;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ConfigDb {

    public static final String LINE = "&a&m-+-------------------------------------------+-";

    public static final String ONLY_PLAYER = "&cOnly players can use this command!";

    public static final ItemStack WAND = new GuiButton(Material.BLAZE_ROD).setName("&b&lWand").setLore("", "&7Select two points to create mine").getItem();

    public static Location loc1;
    public static Location loc2;

    @SuppressWarnings("deprecation")
    public static ItemStack getDefaultPickaxe() {
        ItemStack item = new ItemStack(Material.IRON_PICKAXE);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(Texter.colored("&f&lIron Pickaxe &8&l[&7&lI&8&l]"));
        meta.addItemFlags(
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_ITEM_SPECIFICS,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_DESTROYS
        );
        meta.setUnbreakable(true);

        item.setItemMeta(meta);

        return item;
    }

    // TEST
    public static ItemStack getAdvancedPickaxe() {
        ItemStack item = getDefaultPickaxe();
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.DIG_SPEED, 5, true);
        meta.getPersistentDataContainer().set(PRISON_ENCH_KEY, PersistentDataType.STRING, "test");
        item.setItemMeta(meta);

        return item;
    }

    public static final NamespacedKey PRISON_ENCH_KEY = new NamespacedKey(Prison.INSTANCE, "prison_ench");
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
}
