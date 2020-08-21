package com.thecodemonkey.charginggadgets.common.utils.itemHandler;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.lwjgl.system.CallbackI;

import java.util.Objects;

public class ItemHandlerHelper {
    public static ItemStack insertItem(IItemHandler dest, ItemStack stack, boolean simulate) {
        if (dest == null || stack.isEmpty())
            return stack;

        for (int i = 0; i < dest.getSlots(); i++) {
            stack = dest.insertItem(i, stack, simulate);
            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }

        return stack;
    }

    public static boolean canItemStacksStack(ItemStack a, ItemStack b) {
        if (a.isEmpty() || !a.isItemEqual(b) || a.hasTag() != b.hasTag())
            return false;
        return (!a.hasTag() || Objects.equals(a.getTag(), b.getTag())) && ItemStack.areTagsEqual(a, b);
    }

    public static boolean canItemStacksStackRelaxed(ItemStack a, ItemStack b) {
        if (a.isEmpty() || b.isEmpty() || a.getItem() != b.getItem())
            return false;
        if (!a.isStackable())
            return false;

        if (a.hasTag() != b.hasTag())
            return false;

        return (!a.hasTag() || Objects.equals(a.getTag(), b.getTag())) && ItemStack.areTagsEqual(a, b);

    }

    public static ItemStack copyStackWithSize(ItemStack itemStack, int size) {
        if (size == 0)
            return ItemStack.EMPTY;
        ItemStack copy = itemStack.copy();
        copy.setCount(size);
        return copy;
    }

    public static ItemStack insertItemStacked(IItemHandler inventory, ItemStack stack, boolean simulate) {
        if (inventory == null || stack.isEmpty())
            return stack;
        if (!stack.isStackable()) {
            return insertItem(inventory, stack, simulate);
        }

        int sizeInventory = inventory.getSlots();

        for (int i = 0; i < sizeInventory; i++) {
            ItemStack slot = inventory.getStackInSlot(i);
            if (canItemStacksStackRelaxed(slot, stack)) {
                stack = inventory.insertItem(i, stack, simulate);

                if (stack.isEmpty()) {
                    break;
                }
            }
        }

        if (!stack.isEmpty()) {
            for (int i = 0; i < sizeInventory; i++) {
                if (inventory.getStackInSlot(i).isEmpty()) {
                    stack = inventory.insertItem(i, stack, simulate);
                    if (stack.isEmpty()) {
                        break;
                    }
                }
            }
        }

        return stack;
    }

    public static void giveItemToPlayer(PlayerEntity player, ItemStack stack) {
        giveItemToPlayer(player, stack, -1);
    }

    public static void giveItemToPlayer(PlayerEntity player, ItemStack stack, int preferredSlot) {
        if (stack.isEmpty()) return;

        IItemHandler inventory = new PlayerInventory(player);
        World world = player.world;

        ItemStack remainder = stack;

        if (preferredSlot >= 0 && preferredSlot < inventory.getSlots()) {
            remainder = inventory.insertItem(preferredSlot, stack, false);
        }

        if (!remainder.isEmpty()) {
            remainder = insertItemStacked(inventory, remainder, false);
        }

        if (remainder.isEmpty() || remainder.getCount() != stack.getCount()) {
            world.playSound(null, player.getX(), player.getY() + 0.5, player.getZ(),
                    SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((world.random.nextFloat() - world.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
        }

        if (!remainder.isEmpty() && !world.isClient) {
            ItemEntity entityItem = new ItemEntity(world, player.getX(), player.getY() + 0.5, player.getZ(), remainder);
            entityItem.setPickupDelay(40);
            entityItem.setVelocity(entityItem.getVelocity().multiply(0, 1, 0));

            world.spawnEntity(entityItem);
        }
    }
}
