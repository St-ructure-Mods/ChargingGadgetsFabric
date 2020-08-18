package com.thecodemonkey.charginggadgets.common.tab;

import com.thecodemonkey.charginggadgets.ChargingGadgets;
import com.thecodemonkey.charginggadgets.common.registry.ItemsRegistry;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class CreativeTab {
    public static final ItemGroup TAB = FabricItemGroupBuilder.create(
        new Identifier(ChargingGadgets.MOD_ID, "items"))
        // .icon()
        .appendItems(stacks -> {
            for (Item i: ItemsRegistry.MOD_BLOCKS) {
                stacks.add(new ItemStack(i));
            }

            for (Item i: ItemsRegistry.MOD_ITEMS) {
                stacks.add(new ItemStack(i));
            }
        })
        .build();
}