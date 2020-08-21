package charginggadgets.events;

import charginggadgets.ChargingGadgets;
import net.minecraft.item.Item;

public class ModRegistry {

    public static void setup() {
        registerBlocks();
        registerItems();
    }

    private static void registerBlocks() {
        Item.Settings itemGroup = new Item.Settings().group(ChargingGadgets.ITEMGROUP);

    }

    private static void registerItems() {

    }

}
