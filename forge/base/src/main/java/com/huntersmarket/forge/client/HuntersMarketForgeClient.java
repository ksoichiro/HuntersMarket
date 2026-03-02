package com.huntersmarket.forge.client;

import com.huntersmarket.entity.MerchantEntityRenderer;
import com.huntersmarket.entity.MerchantModel;
import com.huntersmarket.hud.GameHudOverlay;
import com.huntersmarket.registry.ModEntityTypes;
import com.huntersmarket.state.ClientGameState;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;

public class HuntersMarketForgeClient {
    public static void init(IEventBus modBus) {
        // Entity rendering
        modBus.addListener((EntityRenderersEvent.RegisterRenderers event) ->
                event.registerEntityRenderer(ModEntityTypes.MERCHANT.get(), MerchantEntityRenderer::new));
        modBus.addListener((EntityRenderersEvent.RegisterLayerDefinitions event) ->
                event.registerLayerDefinition(MerchantModel.LAYER_LOCATION, MerchantModel::createBodyLayer));

        // HUD
        modBus.addListener((RegisterGuiOverlaysEvent event) ->
                event.registerAboveAll("game_hud",
                        (gui, graphics, tickDelta, width, height) ->
                                GameHudOverlay.render(graphics, tickDelta)));

        // Client disconnect
        MinecraftForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingOut event) -> {
            ClientGameState.reset();
            GameHudOverlay.clearFloatingTexts();
        });
    }
}
