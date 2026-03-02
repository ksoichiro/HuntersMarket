package com.huntersmarket.registry;

import com.huntersmarket.block.GameResetBlock;
import com.huntersmarket.block.GameStartBlock;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

public class ModBlocks {
    public static Supplier<Block> GAME_START_BLOCK;
    public static Supplier<Block> GAME_RESET_BLOCK;
    public static Supplier<Item> GAME_START_BLOCK_ITEM;
    public static Supplier<Item> GAME_RESET_BLOCK_ITEM;

    public static Block createGameStartBlock() {
        return new GameStartBlock(BlockBehaviour.Properties.of().strength(5.0F, 6.0F));
    }

    public static Block createGameStartBlock(ResourceKey<Block> key) {
        return new GameStartBlock(BlockBehaviour.Properties.of().setId(key).strength(5.0F, 6.0F));
    }

    public static Block createGameResetBlock() {
        return new GameResetBlock(BlockBehaviour.Properties.of().strength(5.0F, 6.0F));
    }

    public static Block createGameResetBlock(ResourceKey<Block> key) {
        return new GameResetBlock(BlockBehaviour.Properties.of().setId(key).strength(5.0F, 6.0F));
    }

    public static Item createBlockItem(Block block) {
        return new BlockItem(block, new Item.Properties());
    }

    public static Item createBlockItem(Block block, ResourceKey<Item> key) {
        return new BlockItem(block, new Item.Properties().setId(key));
    }
}
