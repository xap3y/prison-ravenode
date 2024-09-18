package eu.xap3y.prison.storage;

import eu.xap3y.prison.storage.dto.Block;
import eu.xap3y.xagui.models.GuiButton;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class ConfigDb {

    public static final String LINE = "&a&m-+-------------------------------------------+-";

    public static final String ONLY_PLAYER = "&cOnly players can use this command!";

    public static final ItemStack WAND = new GuiButton(Material.BLAZE_ROD).setName("&b&lWand").setLore("", "&7Select two points to create mine").getItem();

    public static Location loc1;
    public static Location loc2;


    public static final HashMap<Material, Block> BLOCK_MAPPER = new HashMap<>() {{
        put(Material.STONE, new Block(50, 10, 10));       // 50%
        put(Material.COBBLESTONE, new Block(50, 20, 20)); // 50%

        put(Material.BRICKS, new Block(50, 20, 20));       // 50%
        put(Material.MUD_BRICKS, new Block(50, 20, 20));   // 50%

        put(Material.DIORITE, new Block(50, 30, 30));       // 50%
        put(Material.POLISHED_DIORITE, new Block(50, 30, 30));   // 50%

        put(Material.ANDESITE, new Block(50, 40, 40));       // 50%
        put(Material.POLISHED_ANDESITE, new Block(50, 40, 40));   // 50%

        put(Material.SAND, new Block(50, 50, 50));       // 50%
        put(Material.SANDSTONE, new Block(50, 50, 50));   // 50%

        put(Material.TERRACOTTA, new Block(50, 60, 60));       // 50%
        put(Material.PACKED_MUD, new Block(50, 60, 60));   // 50%

        put(Material.GOLD_BLOCK, new Block(50, 200, 0));
    }};
}
