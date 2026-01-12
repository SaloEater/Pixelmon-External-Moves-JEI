package com.saloeater.pixelmonjei;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class ConditionIngredientHelper implements IIngredientHelper<ConditionIngredient> {
    @Override
    public IIngredientType<ConditionIngredient> getIngredientType() {
        return null;
    }

    @Override
    public String getDisplayName(ConditionIngredient ingredient) {
        return I18n.get(ingredient.getType().getTranslationKey());
    }

    @Override
    public String getUniqueId(ConditionIngredient ingredient, UidContext context) {
        return ingredient.getType().name() + "_" + ingredient.isAntiCondition();
    }

    @Override
    public String getWildcardId(ConditionIngredient ingredient) {
        return getUniqueId(ingredient, UidContext.Ingredient);
    }

    @Override
    public ResourceLocation getResourceLocation(ConditionIngredient conditionIngredient) {
        return new ResourceLocation(PixelmonJEI.MODID, "condition_" + conditionIngredient.getType().name().toLowerCase());
    }

    @Override
    public ConditionIngredient copyIngredient(ConditionIngredient ingredient) {
        return ingredient;
    }

    @Override
    public String getErrorInfo(@Nullable ConditionIngredient ingredient) {
        if (ingredient == null) {
            return "null";
        }
        return ingredient.getType().name();
    }
}
