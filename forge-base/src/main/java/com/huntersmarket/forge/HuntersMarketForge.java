package com.huntersmarket.forge;

import com.huntersmarket.HuntersMarket;
import com.huntersmarket.forge.client.HuntersMarketForgeClient;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(HuntersMarket.MOD_ID)
public class HuntersMarketForge {
    public HuntersMarketForge() {
        EventBuses.registerModEventBus(HuntersMarket.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        HuntersMarket.init();
        if (FMLEnvironment.dist == Dist.CLIENT) {
            HuntersMarketForgeClient.init();
        }
    }
}
