package com.saloeater.pixelmonjei;

import com.pixelmonmod.pixelmon.api.spawning.SpawnInfo;
import com.pixelmonmod.pixelmon.api.spawning.archetypes.entities.items.SpawnInfoItem;
import com.pixelmonmod.pixelmon.api.spawning.conditions.SpawnCondition;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.*;

public class PixelmonSpawningItemRecipe {
    private final SpawnInfoItem spawnInfoItem;
    private final ItemStack output;
    private final List<SpawnCondition> conditions;
    private final List<SpawnCondition> antiConditions;
    private final int minQuantity;
    private final int maxQuantity;
    private final float rarity;
    private final Float percentage;
    private final Float chance;
    private final int requiredSpace;
    private final Map<ConditionType, List<String>> groupedConditions;
    private final Map<ConditionType, List<String>> groupedAntiConditions;

    public PixelmonSpawningItemRecipe(SpawnInfoItem spawnInfoItem, List<SpawnInfo> parentHierarchy) {
        this.spawnInfoItem = spawnInfoItem;
        this.output = spawnInfoItem.itemStack.copy();
        this.minQuantity = spawnInfoItem.minQuantity;
        this.maxQuantity = spawnInfoItem.maxQuantity;
        this.rarity = spawnInfoItem.rarity;
        this.percentage = spawnInfoItem.percentage;
        this.chance = spawnInfoItem.chance;
        this.requiredSpace = spawnInfoItem.requiredSpace;

        String itemName = output.getItem().getRegistryName().toString().replace(":", "_");

        this.conditions = new ArrayList<>();
        this.antiConditions = new ArrayList<>();

        for (SpawnInfo parent : parentHierarchy) {
            if (parent.condition != null) {
                this.conditions.add(parent.condition);
            }
            if (parent.anticondition != null) {
                this.antiConditions.add(parent.anticondition);
            }
        }

        if (spawnInfoItem.condition != null) {
            this.conditions.add(spawnInfoItem.condition);
        }
        if (spawnInfoItem.anticondition != null) {
            this.antiConditions.add(spawnInfoItem.anticondition);
        }

        this.groupedConditions = new HashMap<>();
        for (SpawnCondition condition : this.conditions) {
            ConditionIconBuilder.groupConditionsByType(condition, this.groupedConditions);
        }

        this.groupedAntiConditions = new HashMap<>();
        for (SpawnCondition antiCondition : this.antiConditions) {
            ConditionIconBuilder.groupConditionsByType(antiCondition, this.groupedAntiConditions);
        }
    }

    public List<ItemStack> getOutputs() {
        ItemStack stack = output.copy();
        int averageCount = (minQuantity + maxQuantity) / 2;
        stack.setCount(averageCount);

        CompoundNBT display = stack.getOrCreateTagElement("display");
        ListNBT lore = new ListNBT();

        if (rarity > 0) {
            lore.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(
                    new StringTextComponent("§7" + I18n.get("pixelmonjei.stat.rarity") + ": §f" + String.format("%.2f", rarity))
            )));
        }

        if (percentage != null) {
            lore.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(
                    new StringTextComponent("§7" + I18n.get("pixelmonjei.stat.percentage") + ": §f" + String.format("%.1f%%", percentage))
            )));
        }

        if (chance != null) {
            lore.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(
                    new StringTextComponent("§7" + I18n.get("pixelmonjei.stat.chance") + ": §f" + String.format("%.1f%%", chance * 100))
            )));
        }

        if (requiredSpace > 0) {
            lore.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(
                    new StringTextComponent("§7" + I18n.get("pixelmonjei.stat.required_space") + ": §f" + requiredSpace)
            )));
        }

        if (lore.size() > 0) {
            display.put("Lore", lore);
        }

        return Collections.singletonList(stack);
    }

    public SpawnInfoItem getSpawnInfoItem() {
        return spawnInfoItem;
    }

    public ItemStack getOutput() {
        return output;
    }

    public List<SpawnCondition> getConditions() {
        return conditions;
    }

    public List<SpawnCondition> getAntiConditions() {
        return antiConditions;
    }

    public int getMinQuantity() {
        return minQuantity;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    public float getRarity() {
        return rarity;
    }

    public Map<ConditionType, List<String>> getGroupedConditions() {
        return groupedConditions;
    }

    public Map<ConditionType, List<String>> getGroupedAntiConditions() {
        return groupedAntiConditions;
    }
}
