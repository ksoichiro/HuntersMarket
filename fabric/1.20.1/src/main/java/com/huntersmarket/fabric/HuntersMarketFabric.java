package com.huntersmarket.fabric;

import com.huntersmarket.HuntersMarket;
import com.huntersmarket.event.GameTickHandler;
import com.huntersmarket.event.PlayerSpawnHandler;
import com.huntersmarket.network.GameStateSyncPacket;
import com.huntersmarket.registry.ModBlocks;
import com.huntersmarket.registry.ModCreativeTab;
import com.huntersmarket.registry.ModEntityTypes;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class HuntersMarketFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        HuntersMarket.init();
        registerAll();
        registerNetworking();
        registerEvents();
    }

    private void registerAll() {
        // Blocks
        ModBlocks.GAME_START_BLOCK = register(BuiltInRegistries.BLOCK,
                new ResourceLocation(HuntersMarket.MOD_ID, "game_start_block"),
                ModBlocks.createGameStartBlock());
        ModBlocks.GAME_RESET_BLOCK = register(BuiltInRegistries.BLOCK,
                new ResourceLocation(HuntersMarket.MOD_ID, "game_reset_block"),
                ModBlocks.createGameResetBlock());

        // Block items
        ModBlocks.GAME_START_BLOCK_ITEM = register(BuiltInRegistries.ITEM,
                new ResourceLocation(HuntersMarket.MOD_ID, "game_start_block"),
                ModBlocks.createBlockItem(ModBlocks.GAME_START_BLOCK.get()));
        ModBlocks.GAME_RESET_BLOCK_ITEM = register(BuiltInRegistries.ITEM,
                new ResourceLocation(HuntersMarket.MOD_ID, "game_reset_block"),
                ModBlocks.createBlockItem(ModBlocks.GAME_RESET_BLOCK.get()));

        // Entity types
        ModEntityTypes.MERCHANT = register(BuiltInRegistries.ENTITY_TYPE,
                new ResourceLocation(HuntersMarket.MOD_ID, "merchant"),
                ModEntityTypes.createMerchantEntityType());
        FabricDefaultAttributeRegistry.register(ModEntityTypes.MERCHANT.get(), Mob.createMobAttributes());

        // Creative tab
        CreativeModeTab tab = FabricItemGroup.builder()
                .title(Component.translatable("itemGroup.huntersmarket"))
                .icon(() -> new ItemStack(Items.IRON_SWORD))
                .displayItems((params, output) -> {
                    output.accept(ModBlocks.GAME_START_BLOCK_ITEM.get());
                    output.accept(ModBlocks.GAME_RESET_BLOCK_ITEM.get());
                })
                .build();
        ModCreativeTab.HUNTERS_MARKET_TAB = register(BuiltInRegistries.CREATIVE_MODE_TAB,
                new ResourceLocation(HuntersMarket.MOD_ID, "huntersmarket_tab"),
                tab);
    }

    private void registerNetworking() {
        GameStateSyncPacket.packetSender = (player, manager) -> {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            GameStateSyncPacket.encode(buf, manager, player);
            ServerPlayNetworking.send(player, GameStateSyncPacket.ID, buf);
        };
    }

    private void registerEvents() {
        ServerWorldEvents.LOAD.register((server, level) -> HuntersMarket.onServerLevelLoad(level));
        ServerLifecycleEvents.SERVER_STARTED.register(HuntersMarket::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPING.register(HuntersMarket::onServerStopping);
        ServerTickEvents.START_SERVER_TICK.register(GameTickHandler::onServerTick);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                PlayerSpawnHandler.onPlayerJoin(handler.getPlayer()));
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) ->
                PlayerSpawnHandler.onPlayerRespawn(newPlayer, !alive));
    }

    @SuppressWarnings("unchecked")
    private static <T> java.util.function.Supplier<T> register(Registry<? super T> registry, ResourceLocation id, T entry) {
        T registered = (T) Registry.register((Registry<Object>) registry, id, entry);
        return () -> registered;
    }
}
