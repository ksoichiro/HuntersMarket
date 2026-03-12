package com.huntersmarket.neoforge;

import com.huntersmarket.HuntersMarket;
import com.huntersmarket.entity.MerchantEntity;
import com.huntersmarket.event.GameTickHandler;
import com.huntersmarket.event.PlayerSpawnHandler;
import com.huntersmarket.network.GameStateSyncPacket;
import com.huntersmarket.neoforge.client.HuntersMarketNeoForgeClient;
import com.huntersmarket.state.ClientGameState;
import com.huntersmarket.registry.ModBlocks;
import com.huntersmarket.registry.ModCreativeTab;
import com.huntersmarket.registry.ModEntityTypes;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@Mod(HuntersMarket.MOD_ID)
public class HuntersMarketNeoForge {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks(HuntersMarket.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(HuntersMarket.MOD_ID);
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, HuntersMarket.MOD_ID);
    private static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB, HuntersMarket.MOD_ID);

    public record GameStatePayload(byte[] data) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<GameStatePayload> TYPE =
                new CustomPacketPayload.Type<>(GameStateSyncPacket.ID);
        public static final StreamCodec<FriendlyByteBuf, GameStatePayload> STREAM_CODEC =
                StreamCodec.of(
                        (buf, payload) -> buf.writeByteArray(payload.data),
                        buf -> new GameStatePayload(buf.readByteArray())
                );

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public HuntersMarketNeoForge(IEventBus modBus) {
        HuntersMarket.init();
        registerAll(modBus);
        registerNetworking(modBus);
        registerEvents();
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            HuntersMarketNeoForgeClient.init(modBus);
        }
    }

    private void registerAll(IEventBus modBus) {
        // Blocks - pass ResourceKey so Properties.setId() is called for 1.21.3+
        Supplier<Block> gameStartBlock = BLOCKS.register("game_start_block", () -> {
            ResourceKey<Block> key = ResourceKey.create(net.minecraft.core.registries.Registries.BLOCK,
                    ResourceLocation.fromNamespaceAndPath(HuntersMarket.MOD_ID, "game_start_block"));
            return ModBlocks.createGameStartBlock(key);
        });
        Supplier<Block> gameResetBlock = BLOCKS.register("game_reset_block", () -> {
            ResourceKey<Block> key = ResourceKey.create(net.minecraft.core.registries.Registries.BLOCK,
                    ResourceLocation.fromNamespaceAndPath(HuntersMarket.MOD_ID, "game_reset_block"));
            return ModBlocks.createGameResetBlock(key);
        });
        ModBlocks.GAME_START_BLOCK = gameStartBlock;
        ModBlocks.GAME_RESET_BLOCK = gameResetBlock;

        // Block items - pass ResourceKey so Item.Properties.setId() is called for 1.21.3+
        Supplier<Item> gameStartBlockItem = ITEMS.register("game_start_block", () -> {
            ResourceKey<Item> key = ResourceKey.create(net.minecraft.core.registries.Registries.ITEM,
                    ResourceLocation.fromNamespaceAndPath(HuntersMarket.MOD_ID, "game_start_block"));
            return ModBlocks.createBlockItem(gameStartBlock.get(), key);
        });
        Supplier<Item> gameResetBlockItem = ITEMS.register("game_reset_block", () -> {
            ResourceKey<Item> key = ResourceKey.create(net.minecraft.core.registries.Registries.ITEM,
                    ResourceLocation.fromNamespaceAndPath(HuntersMarket.MOD_ID, "game_reset_block"));
            return ModBlocks.createBlockItem(gameResetBlock.get(), key);
        });
        ModBlocks.GAME_START_BLOCK_ITEM = gameStartBlockItem;
        ModBlocks.GAME_RESET_BLOCK_ITEM = gameResetBlockItem;

        // Entity types
        Supplier<EntityType<MerchantEntity>> merchant = ENTITY_TYPES.register("merchant", ModEntityTypes::createMerchantEntityType);
        ModEntityTypes.MERCHANT = merchant;

        // Creative tab
        Supplier<CreativeModeTab> tab = CREATIVE_TABS.register("huntersmarket_tab", () ->
                CreativeModeTab.builder()
                        .title(Component.translatable("itemGroup.huntersmarket"))
                        .icon(() -> new ItemStack(Items.IRON_SWORD))
                        .displayItems((params, output) -> {
                            output.accept(ModBlocks.GAME_START_BLOCK_ITEM.get());
                            output.accept(ModBlocks.GAME_RESET_BLOCK_ITEM.get());
                        })
                        .build()
        );
        ModCreativeTab.HUNTERS_MARKET_TAB = tab;

        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        ENTITY_TYPES.register(modBus);
        CREATIVE_TABS.register(modBus);

        modBus.addListener(EntityAttributeCreationEvent.class, event ->
                event.put(ModEntityTypes.MERCHANT.get(), Mob.createMobAttributes().build()));
    }

    private void registerNetworking(IEventBus modBus) {
        modBus.addListener(RegisterPayloadHandlersEvent.class, event -> {
            var registrar = event.registrar(HuntersMarket.MOD_ID);
            registrar.playToClient(GameStatePayload.TYPE, GameStatePayload.STREAM_CODEC,
                    (payload, context) -> context.enqueueWork(() -> {
                        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(payload.data()));
                        ClientGameState.handleSyncPacket(buf);
                    }));
        });

        GameStateSyncPacket.packetSender = (player, manager) -> {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            GameStateSyncPacket.encode(buf, manager, player);
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            buf.release();
            PacketDistributor.sendToPlayer(player, new GameStatePayload(bytes));
        };
    }

    private void registerEvents() {
        IEventBus forgeBus = NeoForge.EVENT_BUS;
        forgeBus.addListener((RegisterCommandsEvent event) ->
                HuntersMarket.registerCommands(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection()));
        forgeBus.addListener(LevelEvent.Load.class, event -> {
            if (event.getLevel() instanceof ServerLevel level) {
                HuntersMarket.onServerLevelLoad(level);
            }
        });
        forgeBus.addListener(ServerStartedEvent.class, event ->
                HuntersMarket.onServerStarted(event.getServer()));
        forgeBus.addListener(ServerStoppingEvent.class, event ->
                HuntersMarket.onServerStopping(event.getServer()));
        forgeBus.addListener(ServerTickEvent.Pre.class, event ->
                GameTickHandler.onServerTick(event.getServer()));
        forgeBus.addListener(PlayerEvent.PlayerRespawnEvent.class, event -> {
            if (event.getEntity() instanceof ServerPlayer player) {
                PlayerSpawnHandler.onPlayerRespawn(player, event.isEndConquered(), null);
            }
        });
        forgeBus.addListener(PlayerEvent.PlayerLoggedInEvent.class, event -> {
            if (event.getEntity() instanceof ServerPlayer player) {
                PlayerSpawnHandler.onPlayerJoin(player);
            }
        });
    }
}
