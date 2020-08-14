package com.thecodemonkey.buildinggadgets.common.tab;

import com.thecodemonkey.buildinggadgets.BuildingGadgets;
import com.thecodemonkey.buildinggadgets.common.registry.ItemsRegistry;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class CreativeTab {
    public static final ItemGroup TAB = FabricItemGroupBuilder.create(
        new Identifier(BuildingGadgets.MOD_ID, "items"))
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