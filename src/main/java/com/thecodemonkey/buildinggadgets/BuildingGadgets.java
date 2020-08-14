package com.thecodemonkey.buildinggadgets;

import com.thecodemonkey.buildinggadgets.common.registry.BlocksRegistry;
import com.thecodemonkey.buildinggadgets.common.registry.ItemsRegistry;

import net.fabricmc.api.ModInitializer;


public class BuildingGadgets implements ModInitializer {
	public final static String MOD_ID = "BuildingGadgets";

    @Override
	public void onInitialize() {
        BlocksRegistry.register();
        ItemsRegistry.register();
    }
}
