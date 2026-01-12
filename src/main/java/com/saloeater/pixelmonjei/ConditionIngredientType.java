package com.saloeater.pixelmonjei;

import mezz.jei.api.ingredients.IIngredientType;

public class ConditionIngredientType implements IIngredientType<ConditionIngredient> {
    public static final ConditionIngredientType INSTANCE = new ConditionIngredientType();

    private ConditionIngredientType() {
    }

    @Override
    public Class<? extends ConditionIngredient> getIngredientClass() {
        return ConditionIngredient.class;
    }
}
