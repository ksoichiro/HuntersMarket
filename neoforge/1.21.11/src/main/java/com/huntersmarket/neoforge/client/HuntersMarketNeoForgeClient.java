package com.huntersmarket.neoforge.client;

import com.huntersmarket.HuntersMarket;
import com.huntersmarket.entity.MerchantEntityRenderer;
import com.huntersmarket.entity.MerchantModel;
import com.huntersmarket.hud.GameHudOverlay;
import com.huntersmarket.registry.ModEntityTypes;
import com.huntersmarket.state.ClientGameState;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class HuntersMarketNeoForgeClient {
    public static void init(IEventBus modBus) {
        // Entity rendering
        modBus.addListener(EntityRenderersEvent.RegisterRenderers.class, event ->
                event.registerEntityRenderer(ModEntityTypes.MERCHANT.get(), MerchantEntityRenderer::new));
        modBus.addListener(EntityRenderersEvent.RegisterLayerDefinitions.class, event ->
                event.registerLayerDefinition(MerchantModel.LAYER_LOCATION, MerchantModel::createBodyLayer));

        // HUD
        modBus.addListener(RegisterGuiLayersEvent.class, event ->
                event.registerAbove(VanillaGuiLayers.HOTBAR,
                        Identifier.fromNamespaceAndPath(HuntersMarket.MOD_ID, "game_hud"),
                        GameHudOverlay::render));

        // Client disconnect
        NeoForge.EVENT_BUS.addListener(PlayerEvent.PlayerLoggedOutEvent.class, event -> {
            ClientGameState.reset();
            GameHudOverlay.clearFloatingTexts();
        });
    }
}
