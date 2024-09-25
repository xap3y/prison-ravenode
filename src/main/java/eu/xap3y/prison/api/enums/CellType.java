package eu.xap3y.prison.api.enums;

public enum CellType {

    STARTER,
    BREAD,
    MANGO,
    FIBER,
    GRASS,
    FLOWER;

    public final int prio;

    CellType() {
        this.prio = ordinal();
    }
}
