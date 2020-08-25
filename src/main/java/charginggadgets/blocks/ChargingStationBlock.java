package charginggadgets.blocks;

import charginggadgets.blockentity.ChargingStationBlockEntity;
import charginggadgets.client.GuiType;
import charginggadgets.init.CGContent;
import jdk.internal.jline.internal.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ChargingStationBlock extends GenericMachineBlock {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public ChargingStationBlock() {
        super(GuiType.CHARGING_STATION, ChargingStationBlockEntity::new);

        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return getDefaultState().with(FACING, context.getPlayerFacing().getOpposite());
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new ChargingStationBlockEntity();
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ChargingStationBlockEntity) {
            ChargingStationBlockEntity chargingStationBlockEntity = (ChargingStationBlockEntity) blockEntity;
            if (!world.isClient && player.isCreative() && chargingStationBlockEntity.getEnergy() > 0) {
                ItemStack itemStack = new ItemStack(CGContent.Machine.CHARGING_STATION);
                CompoundTag tag = chargingStationBlockEntity.serializeEnergy(new CompoundTag());
                if (!tag.isEmpty()) {
                    itemStack.putSubTag("energy", tag);
                }

                ItemEntity itemEntity = new ItemEntity(world, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, itemStack);
                itemEntity.setToDefaultPickupDelay();
                world.spawnEntity(itemEntity);
            }
        }
    }
}
