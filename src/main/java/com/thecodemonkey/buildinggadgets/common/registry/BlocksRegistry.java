package com.thecodemonkey.buildinggadgets.common.registry;

import com.thecodemonkey.buildinggadgets.BuildingGadgets;
import com.thecodemonkey.buildinggadgets.common.tab.CreativeTab;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlocksRegistry {
    public static void register() {}

    private static Block registerBlock(String name, Block block) {
        Registry.register(Registry.BLOCK, new Identifier(BuildingGadgets.MOD_ID, name), block);
        ItemsRegistry.registerItem(name, new BlockItem(block, new Item.Settings().group(CreativeTab.TAB)));
        return block;
    }
}