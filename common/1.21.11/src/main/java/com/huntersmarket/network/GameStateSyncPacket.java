package com.huntersmarket.network;

import com.huntersmarket.HuntersMarket;
import com.huntersmarket.state.ClientGameState;
import com.huntersmarket.state.FinishedPlayer;
import com.huntersmarket.state.GameState;
import com.huntersmarket.state.GameStateManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class GameStateSyncPacket {
    public static final Identifier ID =
            Identifier.fromNamespaceAndPath(HuntersMarket.MOD_ID, "game_state_sync");

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
    }

    public static void applyOnClient(FriendlyByteBuf buf) {
        int stateOrdinal = buf.readInt();
        long salesAmount = buf.readLong();
        int playTime = buf.readInt();
        int finishedCount = buf.readInt();
        List<ClientGameState.FinishedEntry> finishedEntries = new ArrayList<>();
        for (int i = 0; i < finishedCount; i++) {
            String name = buf.readUtf();
            int finishTime = buf.readInt();
            finishedEntries.add(new ClientGameState.FinishedEntry(name, finishTime));
        }
        ClientGameState.update(GameState.values()[stateOrdinal], salesAmount, playTime, finishedEntries);
    }

    public static void sendToPlayer(ServerPlayer player, GameStateManager manager) {
        if (packetSender != null) {
            packetSender.accept(player, manager);
        }
    }
}
