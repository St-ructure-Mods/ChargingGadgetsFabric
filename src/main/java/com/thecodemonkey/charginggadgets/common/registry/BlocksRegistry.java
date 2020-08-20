package com.thecodemonkey.charginggadgets.common.registry;

import com.thecodemonkey.charginggadgets.ChargingGadgets;
import com.thecodemonkey.charginggadgets.common.blocks.ChargingStationBlock;
import com.thecodemonkey.charginggadgets.common.tab.CreativeTab;

import com.thecodemonkey.charginggadgets.common.tiles.ChargingStationTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlocksRegistry {

    public static final Block CHARGING_STATION = new ChargingStationBlock();

    public static final BlockEntityType<ChargingStationTile> CHARGING_STATION_TILE = BlockEntityType.Builder.create(ChargingStationTile::new, CHARGING_STATION).build(null);

    public static void register() {
        registerBlock("charging_station", CHARGING_STATION);
        registerBlockEntity("charging_station_tile", CHARGING_STATION_TILE);
    }

    private static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(String name, BlockEntityType<T> blockEntity) {
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(ChargingGadgets.MOD_ID, name), blockEntity);
        return blockEntity;
    }

    private static Block registerBlock(String name, Block block) {
        Registry.register(Registry.BLOCK, new Identifier(ChargingGadgets.MOD_ID, name), block);
        ItemsRegistry.registerItem(name, new BlockItem(block, new Item.Settings().group(CreativeTab.TAB)));
        return block;
    }
}