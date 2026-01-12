package com.saloeater.pixelmonjei;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.Collection;

public enum ConditionType {
    TIME(Items.CLOCK, "Time"),
    WEATHER(Items.SNOW_BLOCK, "Weather"),
    BIOMES(Items.GRASS_BLOCK, "Biomes"),
    DIMENSIONS(Items.OBSIDIAN, "Dimension"),
    LIGHT_LEVEL(Items.GLOWSTONE, "Light Level"),
    SEES_SKY(Items.GLASS, "Sees Sky"),
    TEMPERATURE(Items.ICE, "Temperature"),
    BASE_BLOCKS(Items.STONE, "Base Blocks"),
    NEARBY_BLOCKS(Items.CHEST, "Nearby Blocks"),
    COORDINATES(Items.COMPASS, "Coordinates"),
    STRUCTURES(Items.BRICKS, "Structures"),
    MOON_PHASE(Items.END_STONE, "Moon Phase"),
    PARTY_HEAD(Items.NAME_TAG, "Party Head"),
    TAG(Items.NAME_TAG, "Tag");

    private final Item item;
    private final String displayName;

    ConditionType(Item item, String displayName) {
        this.item = item;
        this.displayName = displayName;
    }

    public ItemStack createStack() {
        return new ItemStack(item);
    }

    public String getDisplayName() {
        return displayName;
    }
}
