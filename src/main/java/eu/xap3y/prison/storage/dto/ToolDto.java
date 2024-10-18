package eu.xap3y.prison.storage.dto;

import eu.xap3y.prison.api.enums.EnchantType;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Data
@AllArgsConstructor
public class ToolDto {

    public Material material;
    public String name;
    public int level;

    @Nullable
    public EnchantType[] enchants;

    @Nullable
    public Map<Enchantment, Integer> enchantments;

}
