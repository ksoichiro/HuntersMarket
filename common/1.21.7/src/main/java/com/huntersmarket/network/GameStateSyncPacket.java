package com.huntersmarket.network;

import com.huntersmarket.HuntersMarket;
import com.huntersmarket.state.FinishedPlayer;
import com.huntersmarket.state.GameStateManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.function.BiConsumer;

public class GameStateSyncPacket {
    public static final ResourceLocation ID =
            ResourceLocation.fromNamespaceAndPath(HuntersMarket.MOD_ID, "game_state_sync");

    public static BiConsumer<ServerPlayer, GameStateManager> packetSender;

    public static void encode(FriendlyByteBuf buf, GameStateManager manager, ServerPlayer player) {
        buf.writeInt(manager.getState().ordinal());
        buf.writeLong(manager.getSalesAmount(player.getUUID()));
        buf.writeInt(manager.getPlayTime());
        List<FinishedPlayer> finished = manager.getFinishedPlayers();
        buf.writeInt(finished.size());
        for (FinishedPlayer fp : finished) {
            buf.writeUtf(fp.playerName());
            buf.writeInt(fp.finishTimeTicks());
        }
        boolean hasEvent = manager.isPriceEventActive();
        buf.writeBoolean(hasEvent);
        if (hasEvent) {
            buf.writeInt(manager.getPriceEventRemainingTicks());
            buf.writeFloat(manager.getPriceMultiplier());
        }
    }

    public static void sendToPlayer(ServerPlayer player, GameStateManager manager) {
        if (packetSender != null) {
            packetSender.accept(player, manager);
        }
    }
}
