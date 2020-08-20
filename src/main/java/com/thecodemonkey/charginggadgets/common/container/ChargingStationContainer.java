package com.thecodemonkey.charginggadgets.common.container;

import com.thecodemonkey.charginggadgets.common.interfaces.IIntArray;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;

public class ChargingStationContainer extends ScreenHandler {
    private static final int SLOTS = 2;

    public final IIntArray data;

    @Override
    public boolean canUse(PlayerEntity player) {
        return false;
    }
}
