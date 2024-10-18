package eu.xap3y.prison.storage.dto;

import eu.xap3y.prison.storage.ConfigDb;
import eu.xap3y.prison.util.Utils;
import eu.xap3y.xalib.managers.Texter;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;

@SuppressWarnings("ALL")
@Data
public class ToolAttributes {

    public String enchant;

    public String name;

    public HashMap<Enchantment, Integer> enchantMapper = new HashMap<>();

    public Material material;

    public boolean defaultFlags = true;

    public int level;

    public ToolAttributes(String name, Material material, int level) {
        this.name = name;
        this.material = material;
        this.level = level;
    }

    public ItemStack getTool() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setAttributeModifiers(material.getDefaultAttributeModifiers(material.getEquipmentSlot()));

        String name = String.format(ConfigDb.TOOL_NAME_PATTERN, this.name, Utils.intToRoman(level));

        meta.setDisplayName(Texter.colored(name));

        if (defaultFlags)
            meta.addItemFlags(
                    ItemFlag.HIDE_ENCHANTS,
                    ItemFlag.HIDE_ATTRIBUTES,
                    ItemFlag.HIDE_ITEM_SPECIFICS,
                    ItemFlag.HIDE_UNBREAKABLE,
                    ItemFlag.HIDE_DESTROYS
            );

        meta.getPersistentDataContainer().set(ConfigDb.PRISON_TOOL_LEVEL_KEY, PersistentDataType.INTEGER, level);
        meta.setUnbreakable(true);
        item.setItemMeta(meta);

        return item;
    }
}
