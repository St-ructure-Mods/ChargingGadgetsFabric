package com.thecodemonkey.charginggadgets.common.blocks;

import com.thecodemonkey.charginggadgets.common.items.ChargingStationItem;
import com.thecodemonkey.charginggadgets.common.tiles.ChargingStationTile;
import jdk.internal.jline.internal.Nullable;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;

public class ChargingStationBlock extends Block implements BlockEntityProvider {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    public ChargingStationBlock() {
        super(FabricBlockSettings.of(Material.STONE).strength(2f, 2f).breakByTool(FabricToolTags.PICKAXES));

        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING);
    }

    @Nullable
    public BlockState getPlacementState(ItemPlacementContext context) {
        return getDefaultState().with(FACING, context.getPlayerFacing().getOpposite());
    }

    @Override
    @Deprecated
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        BlockEntity be = builder.get(LootContextParameters.BLOCK_ENTITY);

        List<ItemStack> drops = super.getDroppedStacks(state, builder);
        if (be instanceof ChargingStationTile) {
            ChargingStationTile blockEntity = (ChargingStationTile) be;
            drops.stream()
                    .filter(e -> e.getItem() instanceof ChargingStationItem)
                    .findFirst()
                    .ifPresent(e -> e.getOrCreateTag().putInt("energy", blockEntity.energyStorage.getEnergyStored()));
        }

        return drops;
    }

    @Override
    @Deprecated
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ChargingStationTile) {
                ItemScatterer.spawn(world, pos, (ChargingStationTile) blockEntity);
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

//    @Override
//    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
//        if (world.isClient())
//            return ActionResult.SUCCESS;
//        else {
//            BlockEntity be = world.getBlockEntity(pos);
//            if (be instanceof ChargingStationTile) {
//                player.openHandledScreen((ChargingStationTile) be);
//            }
//
//            return ActionResult.CONSUME;
//        }
//    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new ChargingStationTile();
    }
}