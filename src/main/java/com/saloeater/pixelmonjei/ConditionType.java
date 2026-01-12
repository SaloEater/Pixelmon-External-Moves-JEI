package com.saloeater.pixelmonjei;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Collection;

public enum ConditionType {
    TIME(Items.CLOCK, "pixelmonjei.condition.time"),
    WEATHER(Items.SNOW_BLOCK, "pixelmonjei.condition.weather"),
    BIOMES(Items.GRASS_BLOCK, "pixelmonjei.condition.biomes"),
    DIMENSIONS(Items.OBSIDIAN, "pixelmonjei.condition.dimensions"),
    LIGHT_LEVEL(Items.GLOWSTONE, "pixelmonjei.condition.light_level"),
    SEES_SKY(Items.GLASS, "pixelmonjei.condition.sees_sky"),
    TEMPERATURE(Items.ICE, "pixelmonjei.condition.temperature"),
    BASE_BLOCKS(Items.STONE, "pixelmonjei.condition.base_blocks"),
    NEARBY_BLOCKS(Items.CHEST, "pixelmonjei.condition.nearby_blocks"),
    COORDINATES(Items.COMPASS, "pixelmonjei.condition.coordinates"),
    STRUCTURES(Items.BRICKS, "pixelmonjei.condition.structures"),
    MOON_PHASE(Items.END_STONE, "pixelmonjei.condition.moon_phase"),
    PARTY_HEAD(Items.NAME_TAG, "pixelmonjei.condition.party_head"),
    TAG(Items.NAME_TAG, "pixelmonjei.condition.tag"),
    MULTIPLIER(Items.ENCHANTED_BOOK, "pixelmonjei.multiplier"),
    UNKNOWN(Items.BARRIER, "pixelmonjei.condition.unknown");

    private final Item item;
    private final String translationKey;

    ConditionType(Item item, String translationKey) {
        this.item = item;
        this.translationKey = translationKey;
    }

    public ItemStack createStack() {
        return new ItemStack(item);
    }

    public String getTranslationKey() {
        return translationKey;
    }
}
