package com.saloeater.pixelmonjei;

import com.pixelmonmod.pixelmon.api.spawning.conditions.RarityMultiplier;
import com.pixelmonmod.pixelmon.api.spawning.conditions.SpawnCondition;
import com.pixelmonmod.pixelmon.api.world.WeatherType;
import com.pixelmonmod.pixelmon.api.world.WorldTime;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;

import java.util.*;
import java.util.stream.Collectors;

public class ConditionIconBuilder {

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

            if (condition.cachedBaseBlocks != null && !condition.cachedBaseBlocks.isEmpty()) {
                grouped.computeIfAbsent(ConditionType.BASE_BLOCKS, k -> new ArrayList<>())
                        .addAll(formatBlocks(condition.cachedBaseBlocks));
            }

            if (condition.cachedNeededNearbyBlocks != null && !condition.cachedNeededNearbyBlocks.isEmpty()) {
                grouped.computeIfAbsent(ConditionType.NEARBY_BLOCKS, k -> new ArrayList<>())
                        .addAll(formatBlocks(condition.cachedNeededNearbyBlocks));
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

        private static List<String> formatTimes(ArrayList<WorldTime> times) {
            List<String> lines = new ArrayList<>();
            lines.add("§e" + I18n.get("pixelmonjei.condition.time") + ":");
            for (WorldTime time : times) {
                lines.add("  " + I18n.get(time.getTranslationKey()));
            }
            return lines;
        }

        private static List<String> formatWeathers(Set<WeatherType> weathers) {
            List<String> lines = new ArrayList<>();
            lines.add("§e" + I18n.get("pixelmonjei.condition.weather") + ":");
            for (WeatherType weather : weathers) {
                lines.add("  " + I18n.get(weather.getTranslationKey()));
            }
            return lines;
        }

        private static List<String> formatBiomes(Set<ResourceLocation> biomes) {
            List<String> lines = new ArrayList<>();
            lines.add("§e" + I18n.get("pixelmonjei.condition.biomes") + ":");
            List<String> biomeNames = biomes.stream()
                    .map(rl -> "  " + generateTranslation( "biome", rl))
                    .collect(Collectors.toList());

            return biomeNames;
        }

        private static List<String> formatDimensions(Set<ResourceLocation> dimensions) {
            List<String> lines = new ArrayList<>();
            lines.add("§e" + I18n.get("pixelmonjei.condition.dimensions") + ":");
            for (ResourceLocation dim : dimensions) {
                lines.add("  " + generateTranslation( "dimension", dim));
            }
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

        private static List<String> formatBlocks(Set<Block> blocks) {
            List<String> lines = new ArrayList<>();
            lines.add("§e" + I18n.get("pixelmonjei.condition.blocks") + ":");
            List<String> blockNames = blocks.stream()
                    .map(block -> I18n.get(block.getDescriptionId()))
                    .collect(Collectors.toList());

            for (String block : blockNames) {
                lines.add("  " + block);
            }
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
            for (String structure : structures) {
                lines.add("  " + generateTranslation( "structure", new ResourceLocation(structure)));
            }
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
