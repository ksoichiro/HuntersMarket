package com.huntersmarket;

import com.huntersmarket.state.GameStateManager;
import com.huntersmarket.structure.MarketGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HuntersMarket {
    public static final String MOD_ID = "huntersmarket";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        LOGGER.info("Hunter's Market initialized");
    }

    public static void onServerLevelLoad(ServerLevel level) {
        if (level.dimension() == net.minecraft.world.level.Level.OVERWORLD) {
            GameStateManager.init(level);
        }
    }

    public static void onServerStarted(MinecraftServer server) {
        ServerLevel overworld = server.getLevel(net.minecraft.world.level.Level.OVERWORLD);
        if (overworld != null) {
            generateMarketIfNeeded(overworld);
        }
    }

    public static void onServerStopping(MinecraftServer server) {
        GameStateManager.clear();
    }

    private static void generateMarketIfNeeded(ServerLevel level) {
        GameStateManager manager = GameStateManager.getInstance();
        if (manager == null || manager.isMarketGenerated()) return;

        BlockPos spawnPos = level.getRespawnData().pos();
        if (MarketGenerator.generate(level, spawnPos)) {
            manager.setMarketGenerated();
        }
    }
}
