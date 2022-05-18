package charginggadgets.events;

import charginggadgets.ChargingGadgets;
import charginggadgets.init.CGContent.*;
import reborncore.RebornRegistry;

import java.util.Arrays;
import net.minecraft.world.item.Item;

public class ModRegistry {

    public static void setup() {
        registerBlocks();
        registerItems();
    }

    private static void registerBlocks() {
        Item.Properties itemGroup = new Item.Properties().tab(ChargingGadgets.ITEMGROUP);

        Arrays.stream(Machine.values()).forEach(value -> RebornRegistry.registerBlock(value.block, itemGroup));
    }

    private static void registerItems() {

    }

}
