package com.thecodemonkey.charginggadgets.common.registry;

import com.thecodemonkey.charginggadgets.ChargingGadgets;
import com.thecodemonkey.charginggadgets.common.blocks.ChargingStationBlock;
import com.thecodemonkey.charginggadgets.common.tab.CreativeTab;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlocksRegistry {

    public static final Block CHARGING_STATION = new ChargingStationBlock();

    public static void register() {
        registerBlock("charging_station", CHARGING_STATION);
    }

    private static Block registerBlock(String name, Block block) {
        Registry.register(Registry.BLOCK, new Identifier(ChargingGadgets.MOD_ID, name), block);
        ItemsRegistry.registerItem(name, new BlockItem(block, new Item.Settings().group(CreativeTab.TAB)));
        return block;
    }
}