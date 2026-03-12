package com.huntersmarket.forge;

import com.huntersmarket.HuntersMarket;
import com.huntersmarket.entity.MerchantEntity;
import com.huntersmarket.event.GameTickHandler;
import com.huntersmarket.event.PlayerSpawnHandler;
import com.huntersmarket.forge.client.HuntersMarketForgeClient;
import com.huntersmarket.network.GameStateSyncPacket;
import com.huntersmarket.registry.ModBlocks;
import com.huntersmarket.state.ClientGameState;
import com.huntersmarket.registry.ModCreativeTab;
import com.huntersmarket.registry.ModEntityTypes;
import io.netty.buffer.Unpooled;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.function.Supplier;

@Mod(HuntersMarket.MOD_ID)
public class HuntersMarketForge {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, HuntersMarket.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HuntersMarket.MOD_ID);
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, HuntersMarket.MOD_ID);
    private static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, HuntersMarket.MOD_ID);

    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(HuntersMarket.MOD_ID, "main"),
            () -> "1",
            "1"::equals,
            "1"::equals
    );

    public record GameStateMsg(byte[] data) {
        public static void encode(GameStateMsg msg, FriendlyByteBuf buf) {
            buf.writeByteArray(msg.data);
        }

        public static GameStateMsg decode(FriendlyByteBuf buf) {
            return new GameStateMsg(buf.readByteArray());
        }

        public static void handle(GameStateMsg msg, Supplier<NetworkEvent.Context> ctxSupplier) {
            var ctx = ctxSupplier.get();
            ctx.enqueueWork(() -> {
                FriendlyByteBuf friendlyBuf = new FriendlyByteBuf(Unpooled.wrappedBuffer(msg.data));
                ClientGameState.handleSyncPacket(friendlyBuf);
            });
            ctx.setPacketHandled(true);
        }
    }

    public HuntersMarketForge() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        HuntersMarket.init();
        registerAll(modBus);
        registerNetworking();
        registerEvents();
        if (FMLEnvironment.dist == Dist.CLIENT) {
            HuntersMarketForgeClient.init(modBus);
        }
    }

    private void registerAll(IEventBus modBus) {
        // Blocks
        RegistryObject<Block> gameStartBlock = BLOCKS.register("game_start_block", ModBlocks::createGameStartBlock);
        RegistryObject<Block> gameResetBlock = BLOCKS.register("game_reset_block", ModBlocks::createGameResetBlock);
        ModBlocks.GAME_START_BLOCK = gameStartBlock;
        ModBlocks.GAME_RESET_BLOCK = gameResetBlock;

        // Block items
        RegistryObject<Item> gameStartBlockItem = ITEMS.register("game_start_block", () -> ModBlocks.createBlockItem(gameStartBlock.get()));
        RegistryObject<Item> gameResetBlockItem = ITEMS.register("game_reset_block", () -> ModBlocks.createBlockItem(gameResetBlock.get()));
        ModBlocks.GAME_START_BLOCK_ITEM = gameStartBlockItem;
        ModBlocks.GAME_RESET_BLOCK_ITEM = gameResetBlockItem;

        // Entity types
        RegistryObject<EntityType<MerchantEntity>> merchant = ENTITY_TYPES.register("merchant", ModEntityTypes::createMerchantEntityType);
        ModEntityTypes.MERCHANT = merchant;

        // Creative tab
        RegistryObject<CreativeModeTab> tab = CREATIVE_TABS.register("huntersmarket_tab", () ->
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

        modBus.addListener((EntityAttributeCreationEvent event) ->
                event.put(ModEntityTypes.MERCHANT.get(), Mob.createMobAttributes().build()));
    }

    private void registerNetworking() {
        CHANNEL.registerMessage(0, GameStateMsg.class,
                GameStateMsg::encode,
                GameStateMsg::decode,
                GameStateMsg::handle
        );

        GameStateSyncPacket.packetSender = (player, manager) -> {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            GameStateSyncPacket.encode(buf, manager, player);
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            buf.release();
            CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new GameStateMsg(bytes));
        };
    }

    private void registerEvents() {
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener((RegisterCommandsEvent event) ->
                HuntersMarket.registerCommands(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection()));
        forgeBus.addListener((LevelEvent.Load event) -> {
            if (event.getLevel() instanceof ServerLevel level) {
                HuntersMarket.onServerLevelLoad(level);
            }
        });
        forgeBus.addListener((ServerStartedEvent event) ->
                HuntersMarket.onServerStarted(event.getServer()));
        forgeBus.addListener((ServerStoppingEvent event) ->
                HuntersMarket.onServerStopping(event.getServer()));
        forgeBus.addListener((TickEvent.ServerTickEvent event) -> {
            if (event.phase == TickEvent.Phase.START) {
                MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                if (server != null) {
                    GameTickHandler.onServerTick(server);
                }
            }
        });
        forgeBus.addListener((PlayerEvent.PlayerRespawnEvent event) -> {
            if (event.getEntity() instanceof ServerPlayer player) {
                PlayerSpawnHandler.onPlayerRespawn(player, event.isEndConquered());
            }
        });
        forgeBus.addListener((PlayerEvent.PlayerLoggedInEvent event) -> {
            if (event.getEntity() instanceof ServerPlayer player) {
                PlayerSpawnHandler.onPlayerJoin(player);
            }
        });
    }
}
