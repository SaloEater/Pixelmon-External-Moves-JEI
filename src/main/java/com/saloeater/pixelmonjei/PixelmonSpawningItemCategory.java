package com.saloeater.pixelmonjei;

import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class PixelmonSpawningItemCategory implements IRecipeCategory<PixelmonSpawningItemRecipe> {
    private static final int RECIPE_WIDTH = 160;
    private static final int RECIPE_HEIGHT = 125;
    private static final int ITEM_SLOT_X = (RECIPE_WIDTH - 18) / 2;
    private static final int ITEM_SLOT_Y = 5;

    // Labels
    private static final int LABEL_Y = 27;

    // Conditions section (left, 4 columns)
    private static final int CONDITIONS_START_X = 2;
    private static final int CONDITIONS_START_Y = 38;
    private static final int CONDITIONS_SPACING = 20;
    private static final int CONDITIONS_PER_ROW = 4;

    // Gap between conditions and multipliers
    private static final int SECTION_GAP = 10;

    // Multipliers section (right, 2 columns)
    private static final int MULTIPLIERS_START_X = CONDITIONS_START_X + (CONDITIONS_PER_ROW * CONDITIONS_SPACING) + SECTION_GAP;
    private static final int MULTIPLIERS_START_Y = 38;
    private static final int MULTIPLIERS_SPACING = 20;
    private static final int MULTIPLIERS_PER_ROW = 2;

    private final IDrawable background;
    private IDrawable icon;
    private final IDrawable slotDrawable;
    private final String localizedName;

    private final ResourceLocation id;

    public PixelmonSpawningItemCategory(IGuiHelper guiHelper, String localizedName, String id) {
        this.background = guiHelper.createBlankDrawable(RECIPE_WIDTH, RECIPE_HEIGHT);
        this.slotDrawable = guiHelper.getSlotDrawable();
        this.localizedName = localizedName;
        this.id = new ResourceLocation("pixelmonjei", id);
    }

    public PixelmonSpawningItemCategory WithItemAsIcon(IGuiHelper guiHelper, Item item) {
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(item));
        return this;
    }

    public PixelmonSpawningItemCategory WithMoveAsIcon(IGuiHelper guiHelper, ResourceLocation icon) {
        this.icon = guiHelper.drawableBuilder(icon, 0, 0, 16, 16).setTextureSize(16, 16).build();
        return this;
    }


    @Override
    public ResourceLocation getUid() {
        return id;
    }

    @Override
    public Class<? extends PixelmonSpawningItemRecipe> getRecipeClass() {
        return PixelmonSpawningItemRecipe.class;
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
    public void setIngredients(PixelmonSpawningItemRecipe recipe, IIngredients ingredients) {
        ingredients.setOutputs(VanillaTypes.ITEM, recipe.getOutputs());

        java.util.List<ConditionIngredient> conditionIngredients = ConditionIconBuilder.buildIngredients(recipe);
        java.util.List<ConditionIngredient> multiplierIngredients = ConditionIconBuilder.buildMultiplierIngredients(recipe);

        java.util.List<ConditionIngredient> allIngredients = new java.util.ArrayList<>();
        allIngredients.addAll(conditionIngredients);
        allIngredients.addAll(multiplierIngredients);

        ingredients.setInputs(ConditionIngredientType.INSTANCE, allIngredients);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, PixelmonSpawningItemRecipe recipe, IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, false, ITEM_SLOT_X, ITEM_SLOT_Y);
        recipeLayout.getItemStacks().set(0, recipe.getOutputs().get(0));

        java.util.List<ConditionIngredient> conditionIngredients = ConditionIconBuilder.buildIngredients(recipe);
        java.util.List<ConditionIngredient> multiplierIngredients = ConditionIconBuilder.buildMultiplierIngredients(recipe);

        int slotIndex = 0;

        // Layout conditions (4 columns on left)
        for (int i = 0; i < conditionIngredients.size(); i++) {
            int row = i / CONDITIONS_PER_ROW;
            int col = i % CONDITIONS_PER_ROW;
            int x = CONDITIONS_START_X + (col * CONDITIONS_SPACING);
            int y = CONDITIONS_START_Y + (row * CONDITIONS_SPACING);

            recipeLayout.getIngredientsGroup(ConditionIngredientType.INSTANCE)
                    .init(slotIndex, true, x + 1, y + 1);
            recipeLayout.getIngredientsGroup(ConditionIngredientType.INSTANCE)
                    .set(slotIndex, conditionIngredients.get(i));
            slotIndex++;
        }

        // Layout multipliers (2 columns on right)
        for (int i = 0; i < multiplierIngredients.size(); i++) {
            int row = i / MULTIPLIERS_PER_ROW;
            int col = i % MULTIPLIERS_PER_ROW;
            int x = MULTIPLIERS_START_X + (col * MULTIPLIERS_SPACING);
            int y = MULTIPLIERS_START_Y + (row * MULTIPLIERS_SPACING);

            recipeLayout.getIngredientsGroup(ConditionIngredientType.INSTANCE)
                    .init(slotIndex, true, x + 1, y + 1);
            recipeLayout.getIngredientsGroup(ConditionIngredientType.INSTANCE)
                    .set(slotIndex, multiplierIngredients.get(i));
            slotIndex++;
        }
    }

    @Override
    public void draw(PixelmonSpawningItemRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        FontRenderer fontRenderer = Minecraft.getInstance().font;

        // Draw main item slot
        slotDrawable.draw(matrixStack, ITEM_SLOT_X, ITEM_SLOT_Y);

        java.util.List<ConditionIngredient> conditionIngredients = ConditionIconBuilder.buildIngredients(recipe);
        java.util.List<ConditionIngredient> multiplierIngredients = ConditionIconBuilder.buildMultiplierIngredients(recipe);

        // Draw "Conditions" label if there are conditions
        if (!conditionIngredients.isEmpty()) {
            String conditionsLabel = I18n.get("pixelmonjei.label.conditions_header");
            fontRenderer.draw(matrixStack, conditionsLabel, CONDITIONS_START_X, LABEL_Y, 0xFFFFFF);
        }

        // Draw conditions slots
        for (int i = 0; i < conditionIngredients.size(); i++) {
            int row = i / CONDITIONS_PER_ROW;
            int col = i % CONDITIONS_PER_ROW;
            int x = CONDITIONS_START_X + (col * CONDITIONS_SPACING);
            int y = CONDITIONS_START_Y + (row * CONDITIONS_SPACING);

            slotDrawable.draw(matrixStack, x, y);
        }

        // Draw "Multipliers" label if there are multipliers
        if (!multiplierIngredients.isEmpty()) {
            String multipliersLabel = I18n.get("pixelmonjei.label.multipliers_header");
            fontRenderer.draw(matrixStack, multipliersLabel, MULTIPLIERS_START_X, LABEL_Y, 0xFFFFFF);
        }

        // Draw multipliers slots
        for (int i = 0; i < multiplierIngredients.size(); i++) {
            int row = i / MULTIPLIERS_PER_ROW;
            int col = i % MULTIPLIERS_PER_ROW;
            int x = MULTIPLIERS_START_X + (col * MULTIPLIERS_SPACING);
            int y = MULTIPLIERS_START_Y + (row * MULTIPLIERS_SPACING);

            slotDrawable.draw(matrixStack, x, y);
        }
    }
}
