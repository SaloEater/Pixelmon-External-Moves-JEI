package com.saloeater.pixelmonjei;

import com.pixelmonmod.pixelmon.spawning.PixelmonSpawning;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JeiPlugin
public class PixelmonJEIPlugin implements IModPlugin {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final ResourceLocation PLUGIN_UID = new ResourceLocation("pixelmonjei", "plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
            ConditionIngredientHelper ingredientHelper = new ConditionIngredientHelper();
            ConditionIngredientRenderer ingredientRenderer = new ConditionIngredientRenderer();
            registration.register(ConditionIngredientType.INSTANCE, Collections.emptyList(), ingredientHelper, ingredientRenderer);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new PixelmonForageCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<PixelmonForageRecipe> allRecipes = new ArrayList<>();

        PixelmonSpawning.forage.forEach(forageEntry -> {
            forageEntry.forEach(spawnInfo -> {
                List<PixelmonForageRecipe> recipes = ForageRecipeExtractor.extractRecipes(spawnInfo);
                allRecipes.addAll(recipes);
                LOGGER.debug("Extracted {} forage recipes from spawn info: {}", recipes.size(), spawnInfo);
            });
        });

        LOGGER.info("Registering {} Pixelmon Forage recipes in JEI", allRecipes.size());
        registration.addRecipes(allRecipes, PixelmonForageCategory.UID);
    }
}
