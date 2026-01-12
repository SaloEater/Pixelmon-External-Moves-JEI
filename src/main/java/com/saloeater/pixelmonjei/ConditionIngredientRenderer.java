package com.saloeater.pixelmonjei;

import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ConditionIngredientRenderer implements IIngredientRenderer<ConditionIngredient> {
    @Override
    public void render(GuiGraphics guiGraphics, ConditionIngredient conditionIngredient) {
        guiGraphics.renderItem(conditionIngredient.getIcon(), 0, 0);
    }

    @Override
    public List<Component> getTooltip(ConditionIngredient conditionIngredient, TooltipFlag tooltipFlag) {
        List<Component> tooltip = new ArrayList<>();

        for (String line : conditionIngredient.getTooltip()) {
            tooltip.add(Component.literal(line));
        }

        return tooltip;
    }
}
