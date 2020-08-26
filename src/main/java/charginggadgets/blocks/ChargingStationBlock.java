package charginggadgets.blocks;

import charginggadgets.blockentity.ChargingStationBlockEntity;
import charginggadgets.client.GuiType;
import charginggadgets.init.CGContent;
import jdk.internal.jline.internal.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    public void onPlaced(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("blockEntity_data")) {
            BlockEntity blockEntity = worldIn.getBlockEntity(pos);
            if (blockEntity instanceof ChargingStationBlockEntity) {
                CompoundTag nbt = stack.getTag().getCompound("blockEntity_data");
                double energy = stack.getTag().getDouble("energy");
                ((ChargingStationBlockEntity) blockEntity).setEnergy(energy);
                this.injectLocationData(nbt, pos);
                blockEntity.fromTag(state, nbt);
                blockEntity.markDirty();
            }
        }
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        BlockEntity blockEntity = builder.get(LootContextParameters.BLOCK_ENTITY);

        List<ItemStack> drops = super.getDroppedStacks(state, builder);
        if (blockEntity instanceof ChargingStationBlockEntity) {
            ChargingStationBlockEntity chargingStationBlockEntity = (ChargingStationBlockEntity) blockEntity;
            drops.stream()
                    .filter(e -> e.getItem() == CGContent.Machine.CHARGING_STATION.asItem())
                    .findFirst()
                    .ifPresent(e -> e.getOrCreateTag().putDouble("energy", chargingStationBlockEntity.getEnergy()));
        }

        return drops;
    }

    @Override
    public Optional<ItemStack> getDropWithContents(World world, BlockPos pos, ItemStack stack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        System.out.println(blockEntity.getClass());
        if (blockEntity == null) {
            return Optional.empty();
        } else {
            ItemStack newStack = stack.copy();
            CompoundTag blockEntityData = blockEntity.toTag(new CompoundTag());
            this.stripLocationData(blockEntityData);
            if (!newStack.hasTag()) {
                newStack.setTag(new CompoundTag());
            }

            newStack.getTag().put("blockEntity_data", blockEntityData);
            if (blockEntity instanceof ChargingStationBlockEntity) {
                newStack.getTag().putDouble("energy", ((ChargingStationBlockEntity) blockEntity).getEnergy());
            }
            return Optional.of(newStack);
        }
    }

    private void injectLocationData(CompoundTag compound, BlockPos pos) {
        compound.putInt("x", pos.getX());
        compound.putInt("y", pos.getY());
        compound.putInt("z", pos.getZ());
    }

    private void stripLocationData(CompoundTag compound) {
        compound.remove("x");
        compound.remove("y");
        compound.remove("z");
    }
}
