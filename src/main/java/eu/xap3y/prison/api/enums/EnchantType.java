package eu.xap3y.prison.api.enums;

import lombok.Getter;

@Getter
public enum EnchantType {
    TNT("Explosive"),
    BLAST_BREAKER("Blast Breaker"),
    ENHANCER("Enhancer"),
    DEMON("Satanic"),
    ALL("All");

    private final String label;

    EnchantType(String label) {
        this.label = label;
    }
}
