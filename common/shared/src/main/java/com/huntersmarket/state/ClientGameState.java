package com.huntersmarket.state;

import com.huntersmarket.hud.GameHudOverlay;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClientGameState {
    private static GameState state = GameState.NOT_STARTED;
    private static long salesAmount = 0;
    private static int playTime = 0;
    private static List<FinishedEntry> finishedPlayers = Collections.emptyList();
    private static boolean priceEventActive = false;
    private static int priceEventRemainingTicks = 0;
    private static float priceMultiplier = 1.0f;

    public record FinishedEntry(String playerName, int finishTimeTicks) {
    }

    public static void update(GameState state, long salesAmount, int playTime,
                              List<FinishedEntry> finishedPlayers,
                              boolean priceEventActive, int priceEventRemainingTicks,
                              float priceMultiplier) {
        long earned = salesAmount - ClientGameState.salesAmount;
        if (earned > 0) {
            GameHudOverlay.addFloatingText(earned);
        }
        ClientGameState.state = state;
        ClientGameState.salesAmount = salesAmount;
        ClientGameState.playTime = playTime;
        ClientGameState.finishedPlayers = finishedPlayers;
        ClientGameState.priceEventActive = priceEventActive;
        ClientGameState.priceEventRemainingTicks = priceEventRemainingTicks;
        ClientGameState.priceMultiplier = priceMultiplier;
    }

    public static GameState getState() {
        return state;
    }

    public static long getSalesAmount() {
        return salesAmount;
    }

    public static int getPlayTime() {
        return playTime;
    }

    public static List<FinishedEntry> getFinishedPlayers() {
        return finishedPlayers;
    }

    public static boolean isPriceEventActive() {
        return priceEventActive;
    }

    public static int getPriceEventRemainingTicks() {
        return priceEventRemainingTicks;
    }

    public static float getPriceMultiplier() {
        return priceMultiplier;
    }

    public static void handleSyncPacket(FriendlyByteBuf buf) {
        int stateOrdinal = buf.readInt();
        long salesAmount = buf.readLong();
        int playTime = buf.readInt();
        int finishedCount = buf.readInt();
        List<FinishedEntry> finishedEntries = new ArrayList<>();
        for (int i = 0; i < finishedCount; i++) {
            String name = buf.readUtf();
            int finishTime = buf.readInt();
            finishedEntries.add(new FinishedEntry(name, finishTime));
        }
        boolean hasEvent = buf.readBoolean();
        int eventRemainingTicks = 0;
        float multiplier = 1.0f;
        if (hasEvent) {
            eventRemainingTicks = buf.readInt();
            multiplier = buf.readFloat();
        }
        update(GameState.values()[stateOrdinal], salesAmount, playTime,
                finishedEntries, hasEvent, eventRemainingTicks, multiplier);
    }

    public static void reset() {
        state = GameState.NOT_STARTED;
        salesAmount = 0;
        playTime = 0;
        finishedPlayers = Collections.emptyList();
        priceEventActive = false;
        priceEventRemainingTicks = 0;
        priceMultiplier = 1.0f;
    }
}

