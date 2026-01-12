package com.saloeater.pixelmonjei;

import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ConditionIngredientRenderer implements IIngredientRenderer<ConditionIngredient> {

    @Override
    public void render(MatrixStack matrixStack, int xPosition, int yPosition, @Nullable ConditionIngredient ingredient) {
        if (ingredient == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        FontRenderer font = minecraft.font;

        xPosition += 1;
        yPosition += 1;

        itemRenderer.renderAndDecorateItem(ingredient.getIcon(), xPosition, yPosition);
        itemRenderer.renderGuiItemDecorations(font, ingredient.getIcon(), xPosition, yPosition);
    }

    @Override
    public List<ITextComponent> getTooltip(ConditionIngredient ingredient, ITooltipFlag tooltipFlag) {
        List<ITextComponent> tooltip = new ArrayList<>();

        for (String line : ingredient.getTooltip()) {
            tooltip.add(new StringTextComponent(line));
        }

        return tooltip;
    }
}
