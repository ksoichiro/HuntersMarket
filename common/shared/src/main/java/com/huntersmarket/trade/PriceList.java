package com.huntersmarket.trade;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.LinkedHashMap;
import java.util.Map;

public class PriceList {
    private static final Map<Item, Integer> PRICES = new LinkedHashMap<>();

    static {
        PRICES.put(Items.ROTTEN_FLESH, 50);
        PRICES.put(Items.BONE, 50);
        PRICES.put(Items.ARROW, 25);
        PRICES.put(Items.STRING, 25);
        PRICES.put(Items.SPIDER_EYE, 75);
        PRICES.put(Items.GUNPOWDER, 75);
        PRICES.put(Items.ENDER_PEARL, 100);
        PRICES.put(Items.GLOWSTONE_DUST, 100);
        PRICES.put(Items.TRIDENT, 500);
    }

    public static int getPrice(Item item) {
        return PRICES.getOrDefault(item, 0);
    }

    public static boolean isSellable(Item item) {
        return PRICES.containsKey(item);
    }

    public static Map<Item, Integer> getAllPrices() {
        return PRICES;
    }
}
