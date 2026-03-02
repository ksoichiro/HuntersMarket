package com.huntersmarket.fabric.client;

import com.huntersmarket.entity.MerchantEntityRenderer;
import com.huntersmarket.entity.MerchantModel;
import com.huntersmarket.hud.GameHudOverlay;
import com.huntersmarket.network.GameStateSyncPacket;
import com.huntersmarket.registry.ModEntityTypes;
import com.huntersmarket.state.ClientGameState;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class HuntersMarketFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Networking
        ClientPlayNetworking.registerGlobalReceiver(GameStateSyncPacket.ID, (client, handler, buf, responseSender) -> {
            GameStateSyncPacket.applyOnClient(buf);
        });

        // Entity rendering
        EntityModelLayerRegistry.registerModelLayer(MerchantModel.LAYER_LOCATION, MerchantModel::createBodyLayer);
        EntityRendererRegistry.register(ModEntityTypes.MERCHANT.get(), MerchantEntityRenderer::new);

        // HUD (1.20.1 uses (GuiGraphics, float) signature)
        HudRenderCallback.EVENT.register((graphics, tickDelta) -> GameHudOverlay.render(graphics, tickDelta));

        // Client disconnect
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            ClientGameState.reset();
            GameHudOverlay.clearFloatingTexts();
        });
    }
}
