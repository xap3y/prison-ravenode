package eu.xap3y.prison.api.typealias;

import kotlin.Pair;

import java.util.ArrayList;

public class PairArray extends ArrayList<Pair<Float, Integer>> {

    public PairArray() {
        super();
    }

    public float getMultiplier(int index) {
        if (index < 0 || index >= this.size()) {
            return 1f;
        }
        return this.get(index).getFirst();
    }

    public int getChance(int index) {
        if (index < 0 || index >= this.size()) {
            return 0;
        }
        return this.get(index).getSecond();
    }
}
