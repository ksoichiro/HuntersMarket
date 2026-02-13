package com.huntersmarket.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GameStateSavedData extends SavedData {
    GameState state = GameState.NOT_STARTED;
    final Map<UUID, Long> salesAmounts = new HashMap<>();
    final List<FinishedPlayer> finishedPlayers = new ArrayList<>();
    int playTime = 0;
    boolean marketGenerated = false;

    public GameStateSavedData() {
    }

    private static final Codec<FinishedPlayer> FINISHED_PLAYER_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("uuid").forGetter(fp -> fp.playerId().toString()),
                    Codec.STRING.fieldOf("name").forGetter(FinishedPlayer::playerName),
                    Codec.INT.fieldOf("finishTime").forGetter(FinishedPlayer::finishTimeTicks)
            ).apply(instance, (uuid, name, finishTime) -> new FinishedPlayer(UUID.fromString(uuid), name, finishTime))
    );

    public static final Codec<GameStateSavedData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("state").forGetter(d -> d.state.ordinal()),
                    Codec.INT.fieldOf("playTime").forGetter(d -> d.playTime),
                    Codec.BOOL.fieldOf("marketGenerated").forGetter(d -> d.marketGenerated),
                    Codec.unboundedMap(Codec.STRING, Codec.LONG).fieldOf("salesAmounts").forGetter(d -> {
                        Map<String, Long> map = new HashMap<>();
                        d.salesAmounts.forEach((uuid, amount) -> map.put(uuid.toString(), amount));
                        return map;
                    }),
                    FINISHED_PLAYER_CODEC.listOf().fieldOf("finishedPlayers").forGetter(d -> d.finishedPlayers)
            ).apply(instance, (stateOrdinal, playTime, marketGenerated, salesMap, finished) -> {
                GameStateSavedData data = new GameStateSavedData();
                data.state = GameState.values()[stateOrdinal];
                data.playTime = playTime;
                data.marketGenerated = marketGenerated;
                salesMap.forEach((key, amount) -> data.salesAmounts.put(UUID.fromString(key), amount));
                data.finishedPlayers.addAll(finished);
                return data;
            })
    );

    static final SavedDataType<GameStateSavedData> TYPE = new SavedDataType<>(
            "huntersmarket_game_state",
            GameStateSavedData::new,
            CODEC,
            null
    );
}
