package com.saloeater.pixelmonjei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawablesView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.gui.widgets.IScrollGridWidget;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.List;

public class PixelmonSpawningItemCategory extends AbstractRecipeCategory<PixelmonSpawningItemRecipe> {
    private static final int RECIPE_WIDTH = 160;
    private static final int RECIPE_HEIGHT = 125;
    private static final int ITEM_SLOT_X = (RECIPE_WIDTH - 18) / 2;
    private static final int ITEM_SLOT_Y = 5;

    // Conditions section (left side, 4 columns)
    private static final int CONDITIONS_COLUMNS = 4;
    private static final int CONDITIONS_ROWS = 4;
    private static final int CONDITIONS_WIDTH = 82;

    // Multipliers section (right side, 2 columns)
    private static final int MULTIPLIERS_COLUMNS = 2;
    private static final int MULTIPLIERS_ROWS = 4;

    // Gap between sections
    private static final int SECTION_GAP = 8;

    public PixelmonSpawningItemCategory(IGuiHelper guiHelper, String localizedName, String id, IDrawable icon) {
        super(
            RecipeType.create(PixelmonJEI.MODID, id, PixelmonSpawningItemRecipe.class),
            Component.literal(localizedName),
            icon,
            RECIPE_WIDTH,
            RECIPE_HEIGHT
        );
    }

    public static PixelmonSpawningItemCategory withItemAsIcon(IGuiHelper guiHelper, String localizedName, String id, Item item) {
        IDrawable icon = guiHelper.createDrawableItemLike(item);
        return new PixelmonSpawningItemCategory(guiHelper, localizedName, id, icon);
    }

    public static PixelmonSpawningItemCategory withMoveAsIcon(IGuiHelper guiHelper, String localizedName, String id, ResourceLocation iconLocation) {
        IDrawable icon = guiHelper.drawableBuilder(iconLocation, 0, 0, 16, 16).setTextureSize(16, 16).build();
        return new PixelmonSpawningItemCategory(guiHelper, localizedName, id, icon);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PixelmonSpawningItemRecipe recipe, IFocusGroup focuses) {
        // Add output item slot at the top center
        builder.addOutputSlot(ITEM_SLOT_X + 1, ITEM_SLOT_Y + 1)
                .addItemStacks(recipe.getOutputs())
                .setStandardSlotBackground();

        java.util.List<ConditionIngredient> conditionIngredients = ConditionIconBuilder.buildIngredients(recipe);
        java.util.List<ConditionIngredient> multiplierIngredients = ConditionIconBuilder.buildMultiplierIngredients(recipe);

        // Add condition slots (will be arranged in scroll grid later)
        for (ConditionIngredient ingredient : conditionIngredients) {
            builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 0, 0)
                    .addIngredient(ConditionIngredientType.INSTANCE, ingredient);
        }

        // Add multiplier slots (will be arranged in scroll grid later)
        for (ConditionIngredient ingredient : multiplierIngredients) {
            builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 0, 0)
                    .addIngredient(ConditionIngredientType.INSTANCE, ingredient);
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, PixelmonSpawningItemRecipe recipe, IFocusGroup focuses) {
        java.util.List<ConditionIngredient> conditionIngredients = ConditionIconBuilder.buildIngredients(recipe);
        java.util.List<ConditionIngredient> multiplierIngredients = ConditionIconBuilder.buildMultiplierIngredients(recipe);

        IRecipeSlotDrawablesView recipeSlots = builder.getRecipeSlots();
        List<IRecipeSlotDrawable> allSlots = recipeSlots.getSlots(RecipeIngredientRole.RENDER_ONLY);

        // Split slots into conditions and multipliers
        int conditionsCount = conditionIngredients.size();
        List<IRecipeSlotDrawable> conditionSlots = allSlots.subList(0, Math.min(conditionsCount, allSlots.size()));
        List<IRecipeSlotDrawable> multiplierSlots = conditionsCount < allSlots.size()
            ? allSlots.subList(conditionsCount, allSlots.size())
            : List.of();

        // Create conditions scroll grid (left side)
        if (!conditionSlots.isEmpty()) {
            // Add "Conditions" label
            MutableComponent text = Component.literal(I18n.get("pixelmonjei.label.conditions_header"));
            var width = Minecraft.getInstance().font.width(text);
            builder.addText(text, width, 10)
                    .setPosition(2, 28)
                    .setColor(0xFFFFFFFF);

            IScrollGridWidget conditionsGrid = builder.addScrollGridWidget(conditionSlots, CONDITIONS_COLUMNS, CONDITIONS_ROWS);
            conditionsGrid.setPosition(0, 40, width, getHeight() - 40, HorizontalAlignment.LEFT, VerticalAlignment.TOP);
        }

        // Create multipliers scroll grid (right side)
        if (!multiplierSlots.isEmpty()) {
            int multipliersX = CONDITIONS_WIDTH + SECTION_GAP;
            MutableComponent text = Component.literal(I18n.get("pixelmonjei.label.multipliers_header"));
            var width = Minecraft.getInstance().font.width(text);

            // Add "Multipliers" label
            builder.addText(text, width, 10)
                    .setPosition(multipliersX, 28)
                    .setColor(0xFFFFFFFF);

            IScrollGridWidget multipliersGrid = builder.addScrollGridWidget(multiplierSlots, MULTIPLIERS_COLUMNS, MULTIPLIERS_ROWS);
            multipliersGrid.setPosition(multipliersX, 40, width, getHeight() - 40, HorizontalAlignment.LEFT, VerticalAlignment.TOP);
        }
    }

    @Override
    public void draw(PixelmonSpawningItemRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {

    }
}
