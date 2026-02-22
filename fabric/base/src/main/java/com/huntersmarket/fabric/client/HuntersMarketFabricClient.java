package com.huntersmarket.fabric.client;

import com.huntersmarket.HuntersMarket;
import net.fabricmc.api.ClientModInitializer;

public class HuntersMarketFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HuntersMarket.initClient();
    }
}
