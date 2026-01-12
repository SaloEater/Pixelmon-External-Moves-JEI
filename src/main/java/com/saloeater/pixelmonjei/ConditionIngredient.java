package com.saloeater.pixelmonjei;

import net.minecraft.item.ItemStack;

import java.util.List;

public class ConditionIngredient {
    private final ConditionType type;
    private final ItemStack icon;
    private final List<String> tooltip;
    private final boolean isAntiCondition;

    public ConditionIngredient(ConditionType type, ItemStack icon, List<String> tooltip, boolean isAntiCondition) {
        this.type = type;
        this.icon = icon;
        this.tooltip = tooltip;
        this.isAntiCondition = isAntiCondition;
    }

    public ConditionType getType() {
        return type;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public List<String> getTooltip() {
        return tooltip;
    }

    public boolean isAntiCondition() {
        return isAntiCondition;
    }
}
