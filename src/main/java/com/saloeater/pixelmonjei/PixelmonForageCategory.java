package com.saloeater.pixelmonjei;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.pixelmonmod.pixelmon.api.registries.PixelmonItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;

public class PixelmonForageCategory implements IRecipeCategory<PixelmonForageRecipe> {
    public static final ResourceLocation UID = new ResourceLocation("pixelmonjei", "pixelmon_forage");

    private static final int RECIPE_WIDTH = 160;
    private static final int RECIPE_HEIGHT = 125;
    private static final int ITEM_SLOT_X = (RECIPE_WIDTH - 18) / 2;
    private static final int ITEM_SLOT_Y = 5;

    private static final int ICONS_START_X = 10;
    private static final int ICONS_START_Y = 35;
    private static final int ICON_SPACING = 20;
    private static final int ICONS_PER_ROW = 7;

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slotDrawable;
    private final String localizedName;

    public PixelmonForageCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(RECIPE_WIDTH, RECIPE_HEIGHT);
        this.slotDrawable = guiHelper.getSlotDrawable();
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(PixelmonItems.forage_bag));
        this.localizedName = "Pixelmon Forage";
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends PixelmonForageRecipe> getRecipeClass() {
        return PixelmonForageRecipe.class;
    }

    @Override
    public String getTitle() {
        return localizedName;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setIngredients(PixelmonForageRecipe recipe, IIngredients ingredients) {
        ingredients.setOutputs(VanillaTypes.ITEM, recipe.getOutputs());

        java.util.List<ConditionIngredient> conditionIngredients = ConditionIconBuilder.buildIngredients(recipe);
        ingredients.setInputs(ConditionIngredientType.INSTANCE, conditionIngredients);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, PixelmonForageRecipe recipe, IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, false, ITEM_SLOT_X, ITEM_SLOT_Y);
        recipeLayout.getItemStacks().set(0, recipe.getOutputs().get(0));

        java.util.List<ConditionIngredient> conditionIngredients = ConditionIconBuilder.buildIngredients(recipe);

        for (int i = 0; i < conditionIngredients.size(); i++) {
            int row = i / ICONS_PER_ROW;
            int col = i % ICONS_PER_ROW;
            int x = ICONS_START_X + (col * ICON_SPACING);
            int y = ICONS_START_Y + (row * ICON_SPACING);

            recipeLayout.getIngredientsGroup(ConditionIngredientType.INSTANCE)
                    .init(i, true, x, y);
            recipeLayout.getIngredientsGroup(ConditionIngredientType.INSTANCE)
                    .set(i, conditionIngredients.get(i));
        }
    }

    @Override
    public void draw(PixelmonForageRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        slotDrawable.draw(matrixStack, ITEM_SLOT_X, ITEM_SLOT_Y);

        java.util.List<ConditionIngredient> conditionIngredients = ConditionIconBuilder.buildIngredients(recipe);

        for (int i = 0; i < conditionIngredients.size(); i++) {
            int row = i / ICONS_PER_ROW;
            int col = i % ICONS_PER_ROW;
            int x = ICONS_START_X + (col * ICON_SPACING);
            int y = ICONS_START_Y + (row * ICON_SPACING);

            slotDrawable.draw(matrixStack, x, y);
        }
    }
}
