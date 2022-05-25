package charginggadgets.init;

import charginggadgets.blockentity.ChargingStationBlockEntity;
import charginggadgets.blocks.GenericMachineBlock;
import charginggadgets.blocks.GuiType;
import charginggadgets.utils.InitUtils;
import java.util.Locale;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public class CGContent {
    public enum Machine implements ItemLike {
        CHARGING_STATION(new GenericMachineBlock(GuiType.CHARGING_STATION, ChargingStationBlockEntity::new));

        public final String name;
        public final Block block;

        <B extends Block> Machine(B block) {
            this.name = this.toString().toLowerCase(Locale.ROOT);
            this.block = block;
            InitUtils.setup(block, name);
        }

        public ItemStack getStack() {
            return new ItemStack(block);
        }

        @Override
        public Item asItem() {
            return block.asItem();
        }
    }

}
