package eu.xap3y.prison.manager;

import eu.xap3y.prison.api.interfaces.EnchantInterface;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class EnchantManager {

    private static final Map<String, EnchantInterface> enchants = new HashMap<>();

    public static void registerEnchant(@NotNull String name, @NotNull EnchantInterface enchant) {
        enchants.put(name, enchant);
    }

    public static @Nullable EnchantInterface getEnchant(@NotNull String name) {
        return enchants.get(name);
    }
}
