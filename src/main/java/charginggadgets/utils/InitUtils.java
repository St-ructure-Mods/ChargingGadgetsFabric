package charginggadgets.utils;

import charginggadgets.ChargingGadgets;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import reborncore.RebornRegistry;
import reborncore.common.powerSystem.RcEnergyItem;

public class InitUtils {
    public static <I extends Item> I setup(I item, String name) {
        RebornRegistry.registerIdent(item, new ResourceLocation(ChargingGadgets.MOD_ID, name));
        return item;
    }

    public static <B extends Block> B setup(B block, String name) {
        RebornRegistry.registerIdent(block, new ResourceLocation(ChargingGadgets.MOD_ID, name));
        return block;
    }

    public static SoundEvent setup(String name) {
        ResourceLocation identifier = new ResourceLocation(ChargingGadgets.MOD_ID, name);
        return Registry.register(Registry.SOUND_EVENT, identifier, new SoundEvent(identifier));
    }

    public static void initPoweredItems(Item item, NonNullList<ItemStack> itemList) {
        ItemStack uncharged = new ItemStack(item);
        ItemStack charged = new ItemStack(item);
        RcEnergyItem energyItem = (RcEnergyItem) item;

        energyItem.setStoredEnergy(charged, energyItem.getEnergyCapacity());

        itemList.add(uncharged);
        itemList.add(charged);
    }
}
