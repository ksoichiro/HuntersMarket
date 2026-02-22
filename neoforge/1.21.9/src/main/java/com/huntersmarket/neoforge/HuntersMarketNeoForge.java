package com.huntersmarket.neoforge;

import com.huntersmarket.HuntersMarket;
import com.huntersmarket.neoforge.client.HuntersMarketNeoForgeClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(HuntersMarket.MOD_ID)
public class HuntersMarketNeoForge {
    public HuntersMarketNeoForge() {
        HuntersMarket.init();
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            HuntersMarketNeoForgeClient.init();
        }
    }
}
