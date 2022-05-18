package charginggadgets.blocks;

import charginggadgets.blockentity.ChargingStationBlockEntity;
import charginggadgets.client.GuiType;
import charginggadgets.init.CGContent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class ChargingStationBlock extends GenericMachineBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public ChargingStationBlock(BlockPos pos, BlockState state) {
        super(GuiType.CHARGING_STATION, ChargingStationBlockEntity::new);

        registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ChargingStationBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderType() {
        return RenderShape.MODEL;
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("blockEntity_data")) {
            BlockEntity blockEntity = worldIn.getBlockEntity(pos);
            if (blockEntity instanceof ChargingStationBlockEntity) {
                CompoundTag nbt = stack.getTag().getCompound("blockEntity_data");
                long energy = stack.getTag().getLong("energy");
                ((ChargingStationBlockEntity) blockEntity).setEnergy(energy);
                this.injectLocationData(nbt, pos);
                blockEntity.load(nbt);
                blockEntity.setChanged();
            }
        }
    }

    @Override
    @SuppressWarnings("deprecated")
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        BlockEntity blockEntity = builder.getParameter(LootContextParams.BLOCK_ENTITY);

        List<ItemStack> drops = super.getDrops(state, builder);
        if (blockEntity instanceof ChargingStationBlockEntity chargingStationBlockEntity) {
            drops.stream()
                    .filter(e -> e.getItem() == CGContent.Machine.CHARGING_STATION.asItem())
                    .findFirst()
                    .ifPresent(e -> e.getOrCreateTag().putLong("energy", chargingStationBlockEntity.getEnergy()));
        }

        return drops;
    }

    @Override
    public Optional<ItemStack> getDropWithContents(Level world, BlockPos pos, ItemStack stack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity == null) {
            return Optional.empty();
        } else {
            ItemStack newStack = stack.copy();
            CompoundTag blockEntityData = blockEntity.saveWithoutMetadata();
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
