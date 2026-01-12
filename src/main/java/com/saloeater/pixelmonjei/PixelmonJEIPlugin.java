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
import net.minecraft.world.item.Items;
import net.minecraft.resources.ResourceLocation;
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
        categories.put("forage", (new PixelmonSpawningItemCategory(guiHelper, "Pixelmon Forage", "pixelmon_forage")).WithMoveAsIcon(guiHelper, MoveSkill.getMoveSkillByID("forage").sprite));
        categories.put("fishing", (new PixelmonSpawningItemCategory(guiHelper, "Pixelmon Fishing", "pixelmon_fishing").WithItemAsIcon(guiHelper, Items.FISHING_ROD)));
        categories.put("rocksmash", (new PixelmonSpawningItemCategory(guiHelper, "Pixelmon Rock Smash", "pixelmon_rocksmash")).WithMoveAsIcon(guiHelper, MoveSkill.getMoveSkillByID("rock_smash").sprite));
        categories.put("headbutt", (new PixelmonSpawningItemCategory(guiHelper, "Pixelmon Headbutt", "pixelmon_headbutt")).WithMoveAsIcon(guiHelper, MoveSkill.getMoveSkillByID("headbutt").sprite));
        categories.put("sweetscent", (new PixelmonSpawningItemCategory(guiHelper, "Pixelmon Sweet Scent", "pixelmon_sweetscent")).WithMoveAsIcon(guiHelper, MoveSkill.getMoveSkillByID("sweet_scent").sprite));
        categories.put("curry", (new PixelmonSpawningItemCategory(guiHelper, "Pixelmon Curry", "pixelmon_curry")).WithItemAsIcon(guiHelper, PixelmonItems.curry_fried_food));
        categories.put("grass", (new PixelmonSpawningItemCategory(guiHelper, "Pixelmon Grass", "pixelmon_grass")).WithItemAsIcon(guiHelper, PixelmonBlocks.pixelmon_grass.asItem()));
        categories.put("tallgrass", (new PixelmonSpawningItemCategory(guiHelper, "Pixelmon Tall Grass", "pixelmon_tallgrass")).WithItemAsIcon(guiHelper, Items.TALL_GRASS));
        categories.put("seaweed", (new PixelmonSpawningItemCategory(guiHelper, "Pixelmon Seaweed", "pixelmon_seaweed")).WithItemAsIcon(guiHelper, Blocks.SEAGRASS.asItem()));
        categories.put("caverock", (new PixelmonSpawningItemCategory(guiHelper, "Pixelmon Cave Rock", "pixelmon_caverock")).WithItemAsIcon(guiHelper, PixelmonBlocks.cave_rock.asItem()));

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
        registration.addRecipes(allRecipes, category.getUid());
    }
}
