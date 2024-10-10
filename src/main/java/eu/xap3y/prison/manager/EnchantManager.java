package eu.xap3y.prison.manager;

import eu.xap3y.prison.api.enums.EnchantType;
import eu.xap3y.prison.api.interfaces.EnchantInterface;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class EnchantManager {

    private static final Map<EnchantType, EnchantInterface> enchants = new HashMap<>();

    public static void registerEnchant(@NotNull EnchantType type, @NotNull EnchantInterface enchant) {
        enchants.put(type, enchant);
    }

    public static @Nullable EnchantInterface getEnchant(@NotNull EnchantType type) {
        return enchants.get(type);
    }
}
