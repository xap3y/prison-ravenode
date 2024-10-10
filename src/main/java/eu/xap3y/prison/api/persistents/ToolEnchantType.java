package eu.xap3y.prison.api.persistents;

import eu.xap3y.prison.api.enums.EnchantType;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ToolEnchantType implements PersistentDataType<String, EnchantType[]> {

    private static final String DELIMITER = ";";

    @Override
    public @NotNull Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public @NotNull Class<EnchantType[]> getComplexType() {
        return EnchantType[].class;
    }

    @Override
    public @NotNull String toPrimitive(EnchantType @NotNull [] enchantTypes, @NotNull PersistentDataAdapterContext context) {
        return Arrays.stream(enchantTypes)
                .map(Enum::name)
                .collect(Collectors.joining(DELIMITER));
    }

    @Override
    public EnchantType @NotNull [] fromPrimitive(String data, @NotNull PersistentDataAdapterContext context) {
        return Arrays.stream(data.split(DELIMITER))
                .map(EnchantType::valueOf)
                .toArray(EnchantType[]::new);
    }
}
