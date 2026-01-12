package com.saloeater.pixelmonjei;

import com.pixelmonmod.pixelmon.api.spawning.SpawnInfo;
import com.pixelmonmod.pixelmon.api.spawning.archetypes.entities.collection.SpawnInfoCollection;
import com.pixelmonmod.pixelmon.api.spawning.archetypes.entities.items.SpawnInfoItem;

import java.util.ArrayList;
import java.util.List;

public class SpawnInfoRecipeExtractor {

    public static class ItemWithHierarchy {
        public final SpawnInfoItem item;
        public final List<SpawnInfo> parentHierarchy;

        public ItemWithHierarchy(SpawnInfoItem item, List<SpawnInfo> parentHierarchy) {
            this.item = item;
            this.parentHierarchy = new ArrayList<>(parentHierarchy);
        }
    }

    public static List<ItemWithHierarchy> extractItems(SpawnInfo spawnInfo) {
        return extractItems(spawnInfo, new ArrayList<>());
    }

    private static List<ItemWithHierarchy> extractItems(SpawnInfo spawnInfo, List<SpawnInfo> parentHierarchy) {
        List<ItemWithHierarchy> results = new ArrayList<>();

        if (spawnInfo instanceof SpawnInfoItem) {
            results.add(new ItemWithHierarchy((SpawnInfoItem) spawnInfo, parentHierarchy));
        } else if (spawnInfo instanceof SpawnInfoCollection) {
            SpawnInfoCollection collection = (SpawnInfoCollection) spawnInfo;

            List<SpawnInfo> newHierarchy = new ArrayList<>(parentHierarchy);
            newHierarchy.add(collection);

            for (SpawnInfo child : collection.collection) {
                results.addAll(extractItems(child, newHierarchy));
            }
        }

        return results;
    }

    public static List<PixelmonSpawningItemRecipe> extractRecipes(SpawnInfo spawnInfo) {
        List<ItemWithHierarchy> items = extractItems(spawnInfo);
        List<PixelmonSpawningItemRecipe> recipes = new ArrayList<>();

        for (ItemWithHierarchy itemWithHierarchy : items) {
            recipes.add(new PixelmonSpawningItemRecipe(itemWithHierarchy.item, itemWithHierarchy.parentHierarchy));
        }

        return recipes;
    }
}
