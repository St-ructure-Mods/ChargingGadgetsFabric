package com.thecodemonkey.buildinggadgets.common.registry;

import java.util.ArrayList;

import com.thecodemonkey.buildinggadgets.BuildingGadgets;
import com.thecodemonkey.buildinggadgets.common.tab.CreativeTab;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemsRegistry {
    public static final ArrayList<Item> MOD_BLOCKS = new ArrayList<Item>();
    public static final ArrayList<Item> MOD_ITEMS = new ArrayList<Item>();

    public static void register() {}

    public static Item registerItem(String name, Item item)
    {
        Registry.register(Registry.ITEM, new Identifier(BuildingGadgets.MOD_ID, name), item);
        if (item instanceof BlockItem)
            MOD_BLOCKS.add(item);
        else
            MOD_ITEMS.add(item);
        return item;
    }

    public static Item.Settings defaultSettings()
    {
        return new Item.Settings().group(CreativeTab.TAB);
    }
}