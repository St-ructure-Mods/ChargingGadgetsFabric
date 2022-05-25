package charginggadgets.init;

import charginggadgets.ChargingGadgets;
import charginggadgets.blockentity.ChargingStationBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.Validate;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class CGBlockEntities {

    private static final Map<BlockEntityType<?>, ResourceLocation> TYPES = new HashMap<>();

    public static final BlockEntityType<ChargingStationBlockEntity> CHARGING_STATION = register(ChargingStationBlockEntity::new, "charging_station", CGContent.Machine.CHARGING_STATION);

    public static <T extends BlockEntity> BlockEntityType<T> register(BiFunction<BlockPos, BlockState, T> supplier, String name, ItemLike... items) {
        return register(supplier, name, Arrays.stream(items).map(itemConvertible -> Block.byItem(itemConvertible.asItem())).toArray(Block[]::new));
    }

    public static <T extends BlockEntity> BlockEntityType<T> register(BiFunction<BlockPos, BlockState, T> supplier, String name, Block... blocks) {
        Validate.isTrue(blocks.length > 0, "no blocks for blockEntity entity type!");
        return register(new ResourceLocation(ChargingGadgets.MOD_ID, name).toString(), FabricBlockEntityTypeBuilder.create(supplier::apply, blocks));
    }

    public static <T extends BlockEntity> BlockEntityType<T> register(String id, FabricBlockEntityTypeBuilder<T> builder) {
        BlockEntityType<T> blockEntityType = builder.build(null);
        CGBlockEntities.TYPES.put(blockEntityType, new ResourceLocation(id));
        return blockEntityType;
    }

    public static void initBE() {
        TYPES.forEach((blockEntityType, resourceLocation) -> {
            Registry.register(Registry.BLOCK_ENTITY_TYPE, resourceLocation, blockEntityType);
        });
    }
}
