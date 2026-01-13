package com.saloeater.pixelmonjei;

import com.pixelmonmod.pixelmon.api.spawning.conditions.RarityMultiplier;
import com.pixelmonmod.pixelmon.api.spawning.conditions.SpawnCondition;
import com.pixelmonmod.pixelmon.api.world.WeatherType;
import com.pixelmonmod.pixelmon.api.world.WorldTime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.stream.Collectors;

public class ConditionIconBuilder {
    // Cache for resolved biome names from individual TagEntry
    private static final Map<TagEntry, List<String>> biomeNameCache = new HashMap<>();

    // Cache for resolved block names from individual TagEntry
    private static final Map<TagEntry, List<String>> blockNameCache = new HashMap<>();

    /**
     * Clears the caches for resolved biome and block names.
     * Call this after JEI recipe registration or when switching worlds.
     */
    public static void clearCaches() {
        biomeNameCache.clear();
        blockNameCache.clear();
    }

    public static List<ConditionIngredient> buildIngredients(PixelmonSpawningItemRecipe recipe) {
        List<ConditionIngredient> ingredients = new ArrayList<>();
        final int MAX_LINES_PER_ICON = 12;

        Map<ConditionType, List<String>> conditions = recipe.getGroupedConditions();
        registerConditions(conditions, MAX_LINES_PER_ICON, ingredients);

        Map<ConditionType, List<String>> antiConditions = recipe.getGroupedAntiConditions();
        if (!antiConditions.isEmpty()) {

            List<String> header = new ArrayList<>();
            header.add("§c" + I18n.get("pixelmonjei.label.not") + ":");

            ItemStack barrierIcon = new ItemStack(Items.BARRIER);

            registerConditions(antiConditions, MAX_LINES_PER_ICON, ingredients, header, barrierIcon);
        }

        return ingredients;
    }

    public static List<ConditionIngredient> buildMultiplierIngredients(PixelmonSpawningItemRecipe recipe) {
        List<ConditionIngredient> ingredients = new ArrayList<>();
        final int MAX_LINES_PER_ICON = 12;

        List<RarityMultiplier> multipliers = recipe.getRarityMultipliers();

        for (RarityMultiplier multiplier : multipliers) {
            List<String> lines = new ArrayList<>();

            // Add multiplier value as header
            lines.add("§6" + I18n.get("pixelmonjei.label.multiplier") + ": §fx" + String.format("%.2f", multiplier.multiplier));

            // Add conditions if present
            if (multiplier.condition != null) {
                Map<ConditionType, List<String>> conditionGroups = new HashMap<>();
                groupConditionsByType(multiplier.condition, conditionGroups);

                for (Map.Entry<ConditionType, List<String>> entry : conditionGroups.entrySet()) {
                    lines.addAll(entry.getValue());
                }
            }

            // Add anti-conditions if present
            if (multiplier.anticondition != null) {
                lines.add("§c" + I18n.get("pixelmonjei.label.not") + ":");
                Map<ConditionType, List<String>> antiConditionGroups = new HashMap<>();
                groupConditionsByType(multiplier.anticondition, antiConditionGroups);

                for (Map.Entry<ConditionType, List<String>> entry : antiConditionGroups.entrySet()) {
                    lines.addAll(entry.getValue());
                }
            }

            ItemStack icon = ConditionType.MULTIPLIER.createStack();

            // Split into chunks if too long
            if (lines.size() <= MAX_LINES_PER_ICON) {
                ingredients.add(new ConditionIngredient(ConditionType.MULTIPLIER, icon, lines, false));
            } else {
                List<String> header = new ArrayList<>();
                header.add(lines.get(0)); // Keep multiplier value as header
                List<List<String>> chunks = splitIntoChunks(lines, MAX_LINES_PER_ICON, header);
                for (List<String> chunk : chunks) {
                    ingredients.add(new ConditionIngredient(ConditionType.MULTIPLIER, icon, chunk, false));
                }
            }
        }

        return ingredients;
    }

    private static void registerConditions(Map<ConditionType, List<String>> conditions, int MAX_LINES_PER_ICON, List<ConditionIngredient> ingredients) {
        registerConditions(conditions, MAX_LINES_PER_ICON, ingredients, Collections.emptyList(), null);
    }

    private static void registerConditions(Map<ConditionType, List<String>> conditions, int MAX_LINES_PER_ICON, List<ConditionIngredient> ingredients, List<String> header, ItemStack barrierIcon) {
            for (Map.Entry<ConditionType, List<String>> entry : conditions.entrySet()) {
                List<String> allLines = entry.getValue();
                ItemStack icon = entry.getKey().createStack();
                if (barrierIcon != null) {
                    icon = barrierIcon;
                }

                if (allLines.size() <= MAX_LINES_PER_ICON) {
                    List<String> linesWithHeader = new ArrayList<>(header);
                    linesWithHeader.addAll(allLines);
                    ingredients.add(new ConditionIngredient(entry.getKey(), icon, linesWithHeader, false));
                } else {
                    List<String> fullHeader = new ArrayList<>(header);
                    fullHeader.addAll(allLines.subList(0, 1));
                    List<List<String>> chunks = splitIntoChunks(allLines, MAX_LINES_PER_ICON, fullHeader);
                    for (List<String> chunk : chunks) {
                        ingredients.add(new ConditionIngredient(entry.getKey(), icon, chunk, false));
                    }
                }
            }
        }

        private static List<List<String>> splitIntoChunks(List<String> lines, int maxLinesPerChunk, List<String> header) {
            List<List<String>> chunks = new ArrayList<>();

            if (lines.isEmpty()) {
                return chunks;
            }

            List<String> items = lines.subList(1, lines.size());

            int itemsPerChunk = maxLinesPerChunk - 1;
            for (int i = 0; i < items.size(); i += itemsPerChunk) {
                List<String> chunk = new ArrayList<>();
                chunk.addAll(header);

                int end = Math.min(i + itemsPerChunk, items.size());
                chunk.addAll(items.subList(i, end));

                chunks.add(chunk);
            }

            return chunks;
        }

        public static void groupConditionsByType(SpawnCondition condition, Map<ConditionType, List<String>> grouped) {
            // Handle null conditions
            if (condition == null) {
                grouped.computeIfAbsent(ConditionType.UNKNOWN, k -> new ArrayList<>())
                        .add(getUnknownConditionLine());
                return;
            }

            if (condition.times != null && !condition.times.isEmpty()) {
                grouped.computeIfAbsent(ConditionType.TIME, k -> new ArrayList<>())
                        .addAll(formatTimes(condition.times));
            }

            if (condition.cachedWeathers != null && !condition.cachedWeathers.isEmpty()) {
                grouped.computeIfAbsent(ConditionType.WEATHER, k -> new ArrayList<>())
                        .addAll(formatWeathers(condition.cachedWeathers));
            }

            if (condition.biomes != null && !condition.biomes.isEmpty()) {
                grouped.computeIfAbsent(ConditionType.BIOMES, k -> new ArrayList<>())
                        .addAll(formatBiomes(condition.biomes));
            }

            if (condition.cachedDimensions != null && !condition.cachedDimensions.isEmpty()) {
                grouped.computeIfAbsent(ConditionType.DIMENSIONS, k -> new ArrayList<>())
                        .addAll(formatDimensions(condition.cachedDimensions));
            }

            if (condition.minLightLevel != null || condition.maxLightLevel != null) {
                grouped.computeIfAbsent(ConditionType.LIGHT_LEVEL, k -> new ArrayList<>())
                        .addAll(formatLightLevel(condition.minLightLevel, condition.maxLightLevel));
            }

            if (condition.seesSky != null) {
                grouped.computeIfAbsent(ConditionType.SEES_SKY, k -> new ArrayList<>())
                        .addAll(formatSeesSky(condition.seesSky));
            }

            if (condition.temperature != null) {
                grouped.computeIfAbsent(ConditionType.TEMPERATURE, k -> new ArrayList<>())
                        .addAll(formatTemperature(condition.temperature));
            }

            if (condition.baseBlocks != null && !condition.baseBlocks.isEmpty()) {
                grouped.computeIfAbsent(ConditionType.BASE_BLOCKS, k -> new ArrayList<>())
                        .addAll(formatBlocks(condition.baseBlocks));
            }

            if (condition.neededNearbyBlocks != null && !condition.neededNearbyBlocks.isEmpty()) {
                grouped.computeIfAbsent(ConditionType.NEARBY_BLOCKS, k -> new ArrayList<>())
                        .addAll(formatBlocks(condition.neededNearbyBlocks));
            }

            if (condition.minX != null || condition.maxX != null ||
                    condition.minY != null || condition.maxY != null ||
                    condition.minZ != null || condition.maxZ != null) {
                grouped.computeIfAbsent(ConditionType.COORDINATES, k -> new ArrayList<>())
                        .addAll(formatCoordinates(condition));
            }

            if (condition.structures != null && !condition.structures.isEmpty()) {
                grouped.computeIfAbsent(ConditionType.STRUCTURES, k -> new ArrayList<>())
                        .addAll(formatStructures(condition.structures));
            }

            if (condition.moonPhase != null) {
                grouped.computeIfAbsent(ConditionType.MOON_PHASE, k -> new ArrayList<>())
                        .addAll(formatMoonPhase(condition.moonPhase));
            }

            if (condition.tag != null) {
                grouped.computeIfAbsent(ConditionType.TAG, k -> new ArrayList<>())
                        .addAll(formatTag(condition.tag));
            }
        }

    private static String getUnknownConditionLine() {
        return "§c" + I18n.get("pixelmonjei.condition.unknown");
    }

    /**
     * Resolves a set of TagEntry to actual biome names using level registries
     */
    private static List<String> resolveBiomeNames(Set<TagEntry> tagEntries) {
        List<String> resolvedNames = new ArrayList<>();
        Minecraft minecraft = Minecraft.getInstance();

        for (TagEntry entry : tagEntries) {
            if (entry == null) {
                resolvedNames.add(getUnknownConditionLine());
                continue;
            }

            // Check cache first for this individual entry
            if (biomeNameCache.containsKey(entry)) {
                resolvedNames.addAll(biomeNameCache.get(entry));
                continue;
            }

            // Resolve this entry
            List<String> entryNames = new ArrayList<>();

            if (minecraft.level != null) {
                Optional<Registry<Biome>> biomeRegistry = minecraft.level.registryAccess().registry(ForgeRegistries.Keys.BIOMES);

                if (biomeRegistry.isPresent()) {
                    Registry<Biome> registry = biomeRegistry.get();

                    // Get the resource location from the tag entry
                    ResourceLocation location = entry.getId();

                    // Try to get the biome from the registry
                    var biomes = registry.getTag(TagKey.create(ForgeRegistries.Keys.BIOMES, location));

                    if (biomes.isPresent()) {
                        biomes.get().forEach(biome -> {
                            String path = registry.key().location().getPath();
                            entryNames.add(I18n.get(registry.getKey(biome.get()).toLanguageKey("biome")));
                        });
                    } else {
                        var biome = registry.getOptional(location);
                        if (biome.isPresent()) {
                            String path = registry.key().location().getPath();
                            entryNames.add(I18n.get(registry.getKey(biome.get()).toLanguageKey("biome")));
                        } else {
                            // Fallback to the location string
                            entryNames.add(location.toString());
                        }
                    }
                } else {
                    // Fallback: just use toString
                    entryNames.add(entry.toString());
                }
            } else {
                // Fallback: just use toString
                entryNames.add(entry.toString());
            }

            // Cache this individual entry's result
            biomeNameCache.put(entry, entryNames);
            resolvedNames.addAll(entryNames);
        }

        return resolvedNames;
    }

    /**
     * Resolves a set of TagEntry to actual block names using level registries
     */
    private static List<String> resolveBlockNames(Set<TagEntry> tagEntries) {
        List<String> resolvedNames = new ArrayList<>();
        Minecraft minecraft = Minecraft.getInstance();

        for (TagEntry entry : tagEntries) {
            if (entry == null) {
                resolvedNames.add(getUnknownConditionLine());
                continue;
            }

            // Check cache first for this individual entry
            if (blockNameCache.containsKey(entry)) {
                resolvedNames.addAll(blockNameCache.get(entry));
                continue;
            }

            // Resolve this entry
            List<String> entryNames = new ArrayList<>();

            if (minecraft.level != null) {
                Optional<Registry<Block>> blockRegistry = minecraft.level.registryAccess().registry(ForgeRegistries.Keys.BLOCKS);

                if (blockRegistry.isPresent()) {
                    Registry<Block> registry = blockRegistry.get();

                    // Get the resource location from the tag entry
                    ResourceLocation location = entry.getId();

                    // Try to get the block from the registry
                    var blocks = registry.getTag(TagKey.create(ForgeRegistries.Keys.BLOCKS, location));

                    if (blocks.isPresent()) {
                        blocks.get().forEach(block -> {
                            String path = registry.key().registry().getPath();
                            entryNames.add(I18n.get(registry.getKey(block.get()).toLanguageKey(path)));
                        });
                    } else {
                        var block = registry.getOptional(location);
                        if (block.isPresent()) {
                            String path = registry.key().registry().getPath();
                            entryNames.add(I18n.get(registry.getKey(block.get()).toLanguageKey(path)));
                        } else {
                        // Fallback to the location string
                        entryNames.add(location.toString());
                        }
                    }
                } else {
                    // Fallback: just use toString
                    entryNames.add(entry.toString());
                }
            } else {
                // Fallback: just use toString
                entryNames.add(entry.toString());
            }

            // Cache this individual entry's result
            blockNameCache.put(entry, entryNames);
            resolvedNames.addAll(entryNames);
        }

        return resolvedNames;
    }

    private static List<String> formatTimes(ArrayList<WorldTime> times) {
            List<String> lines = new ArrayList<>();
            lines.add("§e" + I18n.get("pixelmonjei.condition.time") + ":");

            List<String> timeStrings = times.stream()
                    .map(time -> "  " + (time == null ? getUnknownConditionLine() : I18n.get(time.getTranslationKey())))
                    .sorted()
                    .collect(Collectors.toList());

            lines.addAll(timeStrings);
            return lines;
        }

        private static List<String> formatWeathers(Set<WeatherType> weathers) {
            List<String> lines = new ArrayList<>();
            lines.add("§e" + I18n.get("pixelmonjei.condition.weather") + ":");

            List<String> weatherStrings = weathers.stream()
                    .map(weather -> "  " + (weather == null ? getUnknownConditionLine() : I18n.get(weather.getTranslationKey())))
                    .sorted()
                    .collect(Collectors.toList());

            lines.addAll(weatherStrings);
            return lines;
        }

        private static List<String> formatBiomes(Set<TagEntry> biomes) {
            List<String> lines = new ArrayList<>();
            lines.add("§e" + I18n.get("pixelmonjei.condition.biomes") + ":");

            // Resolve biome names using level registries with caching
            List<String> resolvedNames = resolveBiomeNames(biomes);

            List<String> biomeNames = resolvedNames.stream()
                    .map(name -> "  " + name)
                    .sorted()
                    .collect(Collectors.toList());

            lines.addAll(biomeNames);
            return lines;
        }

        private static List<String> formatDimensions(Set<ResourceLocation> dimensions) {
            List<String> lines = new ArrayList<>();
            lines.add("§e" + I18n.get("pixelmonjei.condition.dimensions") + ":");

            List<String> dimensionStrings = dimensions.stream()
                    .map(dim -> "  " + (dim == null ? getUnknownConditionLine() : generateTranslation( "dimension", dim)))
                    .sorted()
                    .collect(Collectors.toList());

            lines.addAll(dimensionStrings);
            return lines;
        }

        private static List<String> formatLightLevel(Integer min, Integer max) {
            List<String> lines = new ArrayList<>();
            lines.add("§e" + I18n.get("pixelmonjei.condition.light_level") + ":");
            if (min != null && max != null) {
                lines.add("  " + min + " - " + max);
            } else if (min != null) {
                lines.add("  " + I18n.get("pixelmonjei.label.min") + ": " + min);
            } else if (max != null) {
                lines.add("  " + I18n.get("pixelmonjei.label.max") + ": " + max);
            }
            return lines;
        }

        private static List<String> formatSeesSky(Boolean seesSky) {
            return Arrays.asList(
                    "§e" + I18n.get("pixelmonjei.condition.sees_sky") + ":",
                    "  " + I18n.get(seesSky ? "pixelmonjei.label.yes" : "pixelmonjei.label.no")
            );
        }

        private static List<String> formatTemperature(Float temperature) {
            return Arrays.asList(
                    "§e" + I18n.get("pixelmonjei.condition.temperature") + ":",
                    "  " + temperature
            );
        }

        private static List<String> formatBlocks(Set<TagEntry> blocks) {
            List<String> lines = new ArrayList<>();
            lines.add("§e" + I18n.get("pixelmonjei.condition.blocks") + ":");

            // Resolve block names using ForgeRegistries with caching
            List<String> resolvedNames = resolveBlockNames(blocks);

            List<String> blockNames = resolvedNames.stream()
                    .map(name -> "  " + name)
                    .sorted()
                    .collect(Collectors.toList());

            lines.addAll(blockNames);
            return lines;
        }

        private static List<String> formatCoordinates(SpawnCondition condition) {
            List<String> lines = new ArrayList<>();
            lines.add("§e" + I18n.get("pixelmonjei.condition.coordinates") + ":");
            if (condition.minY != null || condition.maxY != null) {
                String yRange = "";
                if (condition.minY != null && condition.maxY != null) {
                    yRange = "Y: " + condition.minY + " - " + condition.maxY;
                } else if (condition.minY != null) {
                    yRange = "Y: " + condition.minY + "+";
                } else {
                    yRange = "Y: 0 - " + condition.maxY;
                }
                lines.add("  " + yRange);
            }
            if (condition.minX != null || condition.maxX != null) {
                String xRange = "";
                if (condition.minX != null && condition.maxX != null) {
                    xRange = "X: " + condition.minX + " - " + condition.maxX;
                } else if (condition.minX != null) {
                    xRange = "X: " + condition.minX + "+";
                } else {
                    xRange = "X: ... - " + condition.maxX;
                }
                lines.add("  " + xRange);
            }
            if (condition.minZ != null || condition.maxZ != null) {
                String zRange = "";
                if (condition.minZ != null && condition.maxZ != null) {
                    zRange = "Z: " + condition.minZ + " - " + condition.maxZ;
                } else if (condition.minZ != null) {
                    zRange = "Z: " + condition.minZ + "+";
                } else {
                    zRange = "Z: ... - " + condition.maxZ;
                }
                lines.add("  " + zRange);
            }
            return lines;
        }

        private static List<String> formatStructures(ArrayList<String> structures) {
            List<String> lines = new ArrayList<>();
            lines.add("§e" + I18n.get("pixelmonjei.condition.structures") + ":");

            List<String> structureStrings = structures.stream()
                    .map(structure -> "  " + (structure == null ? getUnknownConditionLine() : generateTranslation( "structure", new ResourceLocation(structure))))
                    .sorted()
                    .collect(Collectors.toList());

            lines.addAll(structureStrings);
            return lines;
        }

    private static String generateTranslation(String structurePrefix, ResourceLocation resourceLocation) {
        return I18n.get(structurePrefix + "." + resourceLocation.getNamespace() + "." + resourceLocation.getPath());
    }

    private static List<String> formatMoonPhase(Integer moonPhase) {
            return Arrays.asList(
                    "§e" + I18n.get("pixelmonjei.condition.moon_phase") + ":",
                    "  " + I18n.get("pixelmonjei.label.phase") + " " + moonPhase
            );
        }

        private static List<String> formatTag(String tag) {
            return Arrays.asList(
                    "§e" + I18n.get("pixelmonjei.condition.tag") + ":",
                    "  " + tag
            );
        }
    }
