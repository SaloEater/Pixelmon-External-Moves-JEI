package com.saloeater.pixelmonjei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class PixelmonSpawningItemCategory implements IRecipeCategory<PixelmonSpawningItemRecipe> {
    private static final int RECIPE_WIDTH = 160;
    private static final int RECIPE_HEIGHT = 125;
    private static final int ITEM_SLOT_X = (RECIPE_WIDTH - 18) / 2;
    private static final int ITEM_SLOT_Y = 5;

    // Conditions section (left side, 4 columns)
    private static final int CONDITIONS_START_X = 2;
    private static final int CONDITIONS_START_Y = 38;
    private static final int CONDITIONS_COLUMNS = 4;
    private static final int CONDITIONS_ROWS = 4;
    private static final int CONDITIONS_WIDTH = 82;
    private static final int CONDITIONS_SPACING = 20;

    private static final int SECTION_GAP = 8;

    // Multipliers section (right side, 2 columns)
    private static final int MULTIPLIERS_COLUMNS = 2;
    private static final int MULTIPLIERS_START_X = CONDITIONS_START_X + (CONDITIONS_ROWS * CONDITIONS_SPACING) + SECTION_GAP;
    private static final int MULTIPLIERS_ROWS = 4;

    private static final int LABEL_Y = 27;

    // Gap between sections
    private final RecipeType<PixelmonSpawningItemRecipe> type;
    private final MutableComponent localizedName;
    private final IDrawable icon;
    private final int width;
    private final int heigth;
    private final IDrawable slotDrawable;
    private final IDrawableStatic background;

    public PixelmonSpawningItemCategory(IGuiHelper guiHelper, String localizedName, String id, IDrawable icon) {
        this.type = RecipeType.create(PixelmonJEI.MODID, id, PixelmonSpawningItemRecipe.class);
        this.localizedName = Component.literal(localizedName);
        this.icon = icon;
        this.width = RECIPE_WIDTH;
        this.heigth = RECIPE_HEIGHT;
        this.slotDrawable = guiHelper.getSlotDrawable();
        this.background = guiHelper.createBlankDrawable(RECIPE_WIDTH, RECIPE_HEIGHT);
    }

    public static PixelmonSpawningItemCategory withItemAsIcon(IGuiHelper guiHelper, String localizedName, String id, Item item) {
        IDrawable icon = guiHelper.createDrawableItemStack(new ItemStack(item));
        return new PixelmonSpawningItemCategory(guiHelper, localizedName, id, icon);
    }

    public static PixelmonSpawningItemCategory withMoveAsIcon(IGuiHelper guiHelper, String localizedName, String id, ResourceLocation iconLocation) {
        IDrawable icon = guiHelper.drawableBuilder(iconLocation, 0, 0, 16, 16).setTextureSize(16, 16).build();
        return new PixelmonSpawningItemCategory(guiHelper, localizedName, id, icon);
    }

    @Override
    public RecipeType<PixelmonSpawningItemRecipe> getRecipeType() {
        return type;
    }

    @Override
    public Component getTitle() {
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
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return heigth;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PixelmonSpawningItemRecipe recipe, IFocusGroup focuses) {
        // Add output item slot at the top center
        builder.addSlot(RecipeIngredientRole.OUTPUT, ITEM_SLOT_X + 1, ITEM_SLOT_Y + 1)
                .addItemStacks(recipe.getOutputs());

        java.util.List<ConditionIngredient> conditionIngredients = ConditionIconBuilder.buildIngredients(recipe);
        java.util.List<ConditionIngredient> multiplierIngredients = ConditionIconBuilder.buildMultiplierIngredients(recipe);
        int slotIndex = 0;

        // Layout conditions (4 columns on left)
        for (int i = 0; i < conditionIngredients.size(); i++) {
            var ingredient = conditionIngredients.get(i);
            int row = i / CONDITIONS_ROWS;
            int col = i % CONDITIONS_COLUMNS;
            int x = CONDITIONS_START_X + (col * CONDITIONS_SPACING);
            int y = CONDITIONS_START_Y + (row * CONDITIONS_SPACING);

            builder.addSlot(RecipeIngredientRole.RENDER_ONLY, x + 1, y + 1)
                    .addIngredient(ConditionIngredientType.INSTANCE, ingredient);
            slotIndex++;
        }

        // Layout multipliers (2 columns on right)
        for (int i = 0; i < multiplierIngredients.size(); i++) {
            var ingredient = multiplierIngredients.get(i);
            int row = i / MULTIPLIERS_ROWS;
            int col = i % MULTIPLIERS_COLUMNS;
            int x = MULTIPLIERS_START_X + (col * CONDITIONS_SPACING);
            int y = CONDITIONS_START_Y + (row * CONDITIONS_SPACING);

            builder.addSlot(RecipeIngredientRole.RENDER_ONLY, x + 1, y + 1)
                    .addIngredient(ConditionIngredientType.INSTANCE, ingredient);
            slotIndex++;
        }
    }

    @Override
    public void draw(PixelmonSpawningItemRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        var font = Minecraft.getInstance().font;

        // Draw main item slot
        slotDrawable.draw(guiGraphics, ITEM_SLOT_X, ITEM_SLOT_Y);

        java.util.List<ConditionIngredient> conditionIngredients = ConditionIconBuilder.buildIngredients(recipe);
        java.util.List<ConditionIngredient> multiplierIngredients = ConditionIconBuilder.buildMultiplierIngredients(recipe);

        // Draw "Conditions" label if there are conditions
        if (!conditionIngredients.isEmpty()) {
            String conditionsLabel = I18n.get("pixelmonjei.label.conditions_header");
            guiGraphics.drawString(font, conditionsLabel, CONDITIONS_START_X, LABEL_Y, 0xFFFFFF);
        }

        // Draw conditions slots
        for (int i = 0; i < conditionIngredients.size(); i++) {
            int row = i / CONDITIONS_ROWS;
            int col = i % CONDITIONS_COLUMNS;
            int x = CONDITIONS_START_X + (col * CONDITIONS_SPACING);
            int y = CONDITIONS_START_Y + (row * CONDITIONS_SPACING);

            slotDrawable.draw(guiGraphics, x, y);
        }

        // Draw "Multipliers" label if there are multipliers
        if (!multiplierIngredients.isEmpty()) {
            String multipliersLabel = I18n.get("pixelmonjei.label.multipliers_header");
            guiGraphics.drawString(font, multipliersLabel, MULTIPLIERS_START_X, LABEL_Y, 0xFFFFFF);
        }

        // Draw multipliers slots
        for (int i = 0; i < multiplierIngredients.size(); i++) {
            int row = i / MULTIPLIERS_ROWS;
            int col = i % MULTIPLIERS_COLUMNS;
            int x = MULTIPLIERS_START_X + (col * CONDITIONS_SPACING);
            int y = CONDITIONS_START_Y + (row * CONDITIONS_SPACING);

            slotDrawable.draw(guiGraphics, x, y);
        }
    }
}
