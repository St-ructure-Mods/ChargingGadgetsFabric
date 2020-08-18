package com.thecodemonkey.charginggadgets.common.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.Material;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.Direction;

public class ChargingStationBlock extends Block {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    public ChargingStationBlock() {
        super(FabricBlockSettings.of(Material.STONE).strength(2f, 2f).breakByTool(FabricToolTags.PICKAXES));

        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }
}