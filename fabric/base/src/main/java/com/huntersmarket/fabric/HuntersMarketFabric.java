package com.huntersmarket.fabric;

import com.huntersmarket.HuntersMarket;
import net.fabricmc.api.ModInitializer;

public class HuntersMarketFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        HuntersMarket.init();
    }
}
