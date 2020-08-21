package charginggadgets.init;

import charginggadgets.blockentity.ChargingStationBlockEntity;
import charginggadgets.blocks.GenericMachineBlock;
import charginggadgets.client.GuiType;
import charginggadgets.utils.InitUtils;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;

import java.util.Locale;

public class CGContent {
    public enum Machine implements ItemConvertible {
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
