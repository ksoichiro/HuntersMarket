package com.huntersmarket.registry;

import com.huntersmarket.HuntersMarket;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(HuntersMarket.MOD_ID, Registries.ITEM);

    public static void register() {
        ITEMS.register();
    }
}
