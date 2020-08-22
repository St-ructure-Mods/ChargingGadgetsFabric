package charginggadgets.init;

import charginggadgets.ChargingGadgets;
import charginggadgets.blockentity.ChargingStationBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class CGBlockEntities {

    private static final List<BlockEntityType<?>> TYPES = new ArrayList<>();

    public static final BlockEntityType<ChargingStationBlockEntity> CHARGING_STATION = register(ChargingStationBlockEntity::new, "charging_station", CGContent.Machine.CHARGING_STATION);

    public static <T extends BlockEntity> BlockEntityType<T> register(Supplier<T> supplier, String name, ItemConvertible... items) {
        return register(supplier, name, Arrays.stream(items).map(itemConvertible -> Block.getBlockFromItem(itemConvertible.asItem())).toArray(Block[]::new));
    }

    public static <T extends BlockEntity> BlockEntityType<T> register(Supplier<T> supplier, String name, Block... blocks) {
        Validate.isTrue(blocks.length > 0, "no blocks for blockEntity entity type!");
        return register(new Identifier(ChargingGadgets.MOD_ID, name).toString(), BlockEntityType.Builder.create(supplier, blocks));
    }

    public static <T extends BlockEntity> BlockEntityType<T> register(String id, BlockEntityType.Builder<T> builder) {
        BlockEntityType<T> blockEntityType = builder.build(null);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(id), blockEntityType);
        CGBlockEntities.TYPES.add(blockEntityType);
        return blockEntityType;
    }
}
