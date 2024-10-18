package eu.xap3y.prison.storage.dto;

import eu.xap3y.xagui.models.GuiButton;

import lombok.Data;

@Data
public class UpgradeRes {

    public GuiButton button;
    public boolean upgradable;

    public ToolDto tool;
    public int cost;
    public int newLevel;

    public UpgradeRes(GuiButton button, boolean upgradable) {
        this.button = button;
        this.upgradable = upgradable;
    }
}
