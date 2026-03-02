package com.huntersmarket.fabric.client;

import com.huntersmarket.entity.MerchantEntityRenderer;
import com.huntersmarket.entity.MerchantModel;
import com.huntersmarket.fabric.HuntersMarketFabric;
import com.huntersmarket.hud.GameHudOverlay;
import com.huntersmarket.network.GameStateSyncPacket;
import com.huntersmarket.registry.ModEntityTypes;
import com.huntersmarket.state.ClientGameState;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.network.FriendlyByteBuf;

public class HuntersMarketFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Networking
        ClientPlayNetworking.registerGlobalReceiver(HuntersMarketFabric.GameStatePayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(payload.data()));
                GameStateSyncPacket.applyOnClient(buf);
            });
        });

        // Entity rendering
        EntityModelLayerRegistry.registerModelLayer(MerchantModel.LAYER_LOCATION, MerchantModel::createBodyLayer);
        EntityRendererRegistry.register(ModEntityTypes.MERCHANT.get(), MerchantEntityRenderer::new);

        // HUD
        HudRenderCallback.EVENT.register(GameHudOverlay::render);

        // Client disconnect
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            ClientGameState.reset();
            GameHudOverlay.clearFloatingTexts();
        });
    }
}
