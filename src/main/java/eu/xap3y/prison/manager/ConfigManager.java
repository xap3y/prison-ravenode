package eu.xap3y.prison.manager;

import eu.xap3y.prison.Prison;

public class ConfigManager {

    public static void reloadConfig() {
        if (!Prison.INSTANCE.getDataFolder().exists()) {
            Prison.INSTANCE.getDataFolder().mkdir();
        }

        Prison.INSTANCE.saveDefaultConfig();
        Prison.INSTANCE.reloadConfig();
    }

}
