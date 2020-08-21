package charginggadgets.events;

import charginggadgets.ChargingGadgets;
import charginggadgets.init.CGContent.*;
import net.minecraft.item.Item;
import reborncore.RebornRegistry;

import java.util.Arrays;

public class ModRegistry {

    public static void setup() {
        registerBlocks();
        registerItems();
    }

    private static void registerBlocks() {
        Item.Settings itemGroup = new Item.Settings().group(ChargingGadgets.ITEMGROUP);

        Arrays.stream(Machine.values()).forEach(value -> RebornRegistry.registerBlock(value.block, itemGroup));
    }

    private static void registerItems() {

    }

}
