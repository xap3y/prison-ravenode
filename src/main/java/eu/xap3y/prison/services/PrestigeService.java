package eu.xap3y.prison.services;

import java.util.HashMap;
import java.util.UUID;

public class PrestigeService {

    // Prestiges, XP | money multiplier
    public static final HashMap<Integer, Double> prestigesMapper = new HashMap<>() {{
        put(1, 1.1);
        put(2, 1.25);
        // TODO: Add more prestiges
    }};

    public static boolean canPrestige(UUID p0) {
        return true;
    }
}
