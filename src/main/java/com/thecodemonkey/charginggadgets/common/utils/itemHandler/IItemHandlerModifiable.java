package com.thecodemonkey.charginggadgets.common.utils.itemHandler;

import net.minecraft.item.ItemStack;

public interface IItemHandlerModifiable extends IItemHandler {

    void setStackInSlot(int slot, ItemStack stack);
}
