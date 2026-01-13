package com.saloeater.pixelmonjei;

import com.pixelmonmod.pixelmon.api.moveskills.MoveSkill;
import com.pixelmonmod.pixelmon.api.registries.PixelmonBlocks;
import com.pixelmonmod.pixelmon.api.registries.PixelmonItems;
import com.pixelmonmod.pixelmon.api.spawning.SpawnSet;
import com.pixelmonmod.pixelmon.spawning.PixelmonSpawning;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

@JeiPlugin
public class PixelmonJEIPlugin implements IModPlugin {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final ResourceLocation PLUGIN_UID = new ResourceLocation("pixelmonjei", "plugin");
    private Map<String, PixelmonSpawningItemCategory> categories = new HashMap<>();

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
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        categories.put("forage", PixelmonSpawningItemCategory.withMoveAsIcon(guiHelper, "Pixelmon Forage", "pixelmon_forage", MoveSkill.getMoveSkillByID("forage").sprite()));
        categories.put("fishing", PixelmonSpawningItemCategory.withItemAsIcon(guiHelper, "Pixelmon Fishing", "pixelmon_fishing", Items.FISHING_ROD));
        categories.put("rocksmash", PixelmonSpawningItemCategory.withMoveAsIcon(guiHelper, "Pixelmon Rock Smash", "pixelmon_rocksmash", MoveSkill.getMoveSkillByID("rock_smash").sprite()));
        categories.put("headbutt", PixelmonSpawningItemCategory.withMoveAsIcon(guiHelper, "Pixelmon Headbutt", "pixelmon_headbutt", MoveSkill.getMoveSkillByID("headbutt").sprite()));
        categories.put("sweetscent", PixelmonSpawningItemCategory.withMoveAsIcon(guiHelper, "Pixelmon Sweet Scent", "pixelmon_sweetscent", MoveSkill.getMoveSkillByID("sweet_scent").sprite()));
        categories.put("curry", PixelmonSpawningItemCategory.withItemAsIcon(guiHelper, "Pixelmon Curry", "pixelmon_curry", PixelmonItems.curry_fried_food));
        categories.put("grass", PixelmonSpawningItemCategory.withItemAsIcon(guiHelper, "Pixelmon Grass", "pixelmon_grass", PixelmonBlocks.pixelmon_grass.asItem()));
        categories.put("tallgrass", PixelmonSpawningItemCategory.withItemAsIcon(guiHelper, "Pixelmon Tall Grass", "pixelmon_tallgrass", Items.TALL_GRASS));
        categories.put("seaweed", PixelmonSpawningItemCategory.withItemAsIcon(guiHelper, "Pixelmon Seaweed", "pixelmon_seaweed", Blocks.SEAGRASS.asItem()));
        categories.put("caverock", PixelmonSpawningItemCategory.withItemAsIcon(guiHelper, "Pixelmon Cave Rock", "pixelmon_caverock", PixelmonBlocks.cave_rock.asItem()));

        for (PixelmonSpawningItemCategory category : categories.values()) {
            registration.addRecipeCategories(category);
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Map<PixelmonSpawningItemCategory, List<SpawnSet>> spawnSets = new HashMap<>();
        spawnSets.put(categories.get("forage"), PixelmonSpawning.forage);
        spawnSets.put(categories.get("fishing"), PixelmonSpawning.fishing);
        spawnSets.put(categories.get("rocksmash"), PixelmonSpawning.rocksmash);
        spawnSets.put(categories.get("headbutt"), PixelmonSpawning.headbutt);
        spawnSets.put(categories.get("sweetscent"), PixelmonSpawning.sweetscent);
        spawnSets.put(categories.get("curry"), PixelmonSpawning.curry);
        spawnSets.put(categories.get("grass"), PixelmonSpawning.grass);
        spawnSets.put(categories.get("tallgrass"), PixelmonSpawning.tallgrass);
        spawnSets.put(categories.get("seaweed"), PixelmonSpawning.seaweed);
        spawnSets.put(categories.get("caverock"), PixelmonSpawning.caveRock);

        for (var entry : spawnSets.entrySet()) {
            List<SpawnSet> spawnSet = entry.getValue();
            registerSpawnSet(registration, spawnSet, entry.getKey());
        }

        // Clear caches after all recipes are registered
        ConditionIconBuilder.clearCaches();
        LOGGER.info("Cleared ConditionIconBuilder caches after recipe registration");
    }

    private static void registerSpawnSet(IRecipeRegistration registration, List<SpawnSet> spawnSet, PixelmonSpawningItemCategory category) {
        List<PixelmonSpawningItemRecipe> allRecipes = new ArrayList<>();

        spawnSet.forEach(forageEntry -> {
            forageEntry.forEach(spawnInfo -> {
                List<PixelmonSpawningItemRecipe> recipes = SpawnInfoRecipeExtractor.extractRecipes(spawnInfo);
                allRecipes.addAll(recipes);
                LOGGER.debug("Extracted {} forage recipes from spawn info: {}", recipes.size(), spawnInfo);
            });
        });

        LOGGER.info("Registering {} Pixelmon Forage recipes in JEI", allRecipes.size());
        registration.addRecipes(category.getRecipeType(), allRecipes);
    }
}
