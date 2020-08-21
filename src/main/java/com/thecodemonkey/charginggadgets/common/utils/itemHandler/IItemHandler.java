package com.thecodemonkey.charginggadgets.common.utils.itemHandler;

import net.minecraft.item.ItemStack;

public interface IItemHandler {

    int getSlots();

    ItemStack getStackInSlot(int slot);

    ItemStack insertItem(int slot, ItemStack stack, boolean simulate);

    ItemStack extractItem(int slot, int amount, boolean simulate);

    int getSlotLimit(int slot);

    boolean isItemValid(int slot, ItemStack stack);
}
