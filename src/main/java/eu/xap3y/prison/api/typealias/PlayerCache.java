package eu.xap3y.prison.api.typealias;

import org.apache.commons.lang3.tuple.MutablePair;

import java.util.HashMap;
import java.util.UUID;

public class PlayerCache extends HashMap<UUID, MutablePair<Double, Float>> {

    public PlayerCache() {
        super();
    }

    public float getMultiplier(UUID id) {
        return this.get(id).getRight();
    }

    public double getRequiredXp(UUID id) {
        return this.get(id).getLeft();
    }

    public void setMultiplier(UUID id, float multiplier) {
        this.get(id).setRight(multiplier);
    }

    public void setRequiredXp(UUID id, double requiredXp) {
        this.get(id).setLeft(requiredXp);
    }

    public void put(UUID id, double requiredXp, float multiplier) {
        this.put(id, new MutablePair<>(requiredXp, multiplier));
    }

}
