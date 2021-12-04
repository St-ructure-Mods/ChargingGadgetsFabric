package charginggadgets.blocks;

import charginggadgets.blockentity.ChargingStationBlockEntity;
import charginggadgets.client.GuiType;
import charginggadgets.init.CGContent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ChargingStationBlock extends GenericMachineBlock {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public ChargingStationBlock(BlockPos pos, BlockState state) {
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
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ChargingStationBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onPlaced(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (stack.hasNbt() && stack.getNbt().contains("blockEntity_data")) {
            BlockEntity blockEntity = worldIn.getBlockEntity(pos);
            if (blockEntity instanceof ChargingStationBlockEntity) {
                NbtCompound nbt = stack.getNbt().getCompound("blockEntity_data");
                long energy = stack.getNbt().getLong("energy");
                ((ChargingStationBlockEntity) blockEntity).setEnergy(energy);
                this.injectLocationData(nbt, pos);
                blockEntity.readNbt(nbt);
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
                    .ifPresent(e -> e.getOrCreateNbt().putLong("energy", chargingStationBlockEntity.getEnergy()));
        }

        return drops;
    }

    @Override
    public Optional<ItemStack> getDropWithContents(World world, BlockPos pos, ItemStack stack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity == null) {
            return Optional.empty();
        } else {
            ItemStack newStack = stack.copy();
            NbtCompound blockEntityData = blockEntity.writeNbt(new NbtCompound());
            this.stripLocationData(blockEntityData);
            if (!newStack.hasNbt()) {
                newStack.setNbt(new NbtCompound());
            }

            newStack.getNbt().put("blockEntity_data", blockEntityData);
            if (blockEntity instanceof ChargingStationBlockEntity) {
                newStack.getNbt().putDouble("energy", ((ChargingStationBlockEntity) blockEntity).getEnergy());
            }
            return Optional.of(newStack);
        }
    }

    private void injectLocationData(NbtCompound compound, BlockPos pos) {
        compound.putInt("x", pos.getX());
        compound.putInt("y", pos.getY());
        compound.putInt("z", pos.getZ());
    }

    private void stripLocationData(NbtCompound compound) {
        compound.remove("x");
        compound.remove("y");
        compound.remove("z");
    }
}
