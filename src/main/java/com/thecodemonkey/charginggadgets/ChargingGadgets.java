package com.thecodemonkey.charginggadgets;

import com.thecodemonkey.charginggadgets.common.registry.BlocksRegistry;
import com.thecodemonkey.charginggadgets.common.registry.ItemsRegistry;

import net.fabricmc.api.ModInitializer;

public class ChargingGadgets implements ModInitializer {
	public final static String MOD_ID = "ChargingGadgets";

    @Override
	public void onInitialize() {
        BlocksRegistry.register();
        ItemsRegistry.register();
    }
}
