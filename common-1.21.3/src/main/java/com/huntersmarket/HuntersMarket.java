package com.huntersmarket;

import com.huntersmarket.entity.MerchantEntityRenderer;
import com.huntersmarket.entity.MerchantModel;
import com.huntersmarket.event.GameTickHandler;
import com.huntersmarket.event.PlayerSpawnHandler;
import com.huntersmarket.hud.GameHudOverlay;
import com.huntersmarket.network.GameStateSyncPacket;
import com.huntersmarket.registry.ModBlocks;
import com.huntersmarket.registry.ModCreativeTab;
import com.huntersmarket.registry.ModEntityTypes;
import com.huntersmarket.registry.ModItems;
import com.huntersmarket.state.GameStateManager;
import com.huntersmarket.structure.MarketGenerator;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HuntersMarket {
    public static final String MOD_ID = "huntersmarket";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        // ModBlocks must be registered before ModItems because ModBlocks
        // adds block items to ModItems.ITEMS during class loading
        ModBlocks.register();
        ModItems.register();
        ModEntityTypes.register();
        ModCreativeTab.register();

        LifecycleEvent.SERVER_LEVEL_LOAD.register(level -> {
            if (level.dimension() == net.minecraft.world.level.Level.OVERWORLD) {
                GameStateManager.init(level);
            }
        });
        LifecycleEvent.SERVER_STARTED.register(server -> {
            ServerLevel overworld = server.getLevel(net.minecraft.world.level.Level.OVERWORLD);
            if (overworld != null) {
                generateMarketIfNeeded(overworld);
            }
        });
        LifecycleEvent.SERVER_STOPPING.register(server -> GameStateManager.clear());
        TickEvent.SERVER_PRE.register(GameTickHandler::onServerTick);
        PlayerEvent.PLAYER_RESPAWN.register(PlayerSpawnHandler::onPlayerRespawn);
        PlayerEvent.PLAYER_JOIN.register(PlayerSpawnHandler::onPlayerJoin);

        LOGGER.info("Hunter's Market initialized");
    }

    private static void generateMarketIfNeeded(ServerLevel level) {
        GameStateManager manager = GameStateManager.getInstance();
        if (manager == null || manager.isMarketGenerated()) return;

        BlockPos spawnPos = level.getSharedSpawnPos();
        if (MarketGenerator.generate(level, spawnPos)) {
            manager.setMarketGenerated();
        }
    }

    public static void initClient() {
        GameStateSyncPacket.registerClientReceiver();
        EntityModelLayerRegistry.register(MerchantModel.LAYER_LOCATION, MerchantModel::createBodyLayer);
        EntityRendererRegistry.register(ModEntityTypes.MERCHANT, MerchantEntityRenderer::new);
        ClientGuiEvent.RENDER_HUD.register(GameHudOverlay::render);
    }
}
