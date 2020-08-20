package com.thecodemonkey.charginggadgets.common.items;

import com.thecodemonkey.charginggadgets.common.ModConfig;
import com.thecodemonkey.charginggadgets.common.tiles.ChargingStationTile;
import com.thecodemonkey.charginggadgets.common.utils.MagicHelpers;
import jdk.internal.jline.internal.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class ChargingStationItem extends BlockItem {

    public ChargingStationItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        int power = stack.getOrCreateTag().getInt("energy");
        if (power == 0)
            return;

        tooltip.add(
                new TranslatableText("screen.charginggadgets.energy",
                        MagicHelpers.withSuffix(power),
                        MagicHelpers.withSuffix(ModConfig.chargerMaxPower)
                )
                .setStyle(
                        new TranslatableText("")
                                .getStyle()
                                .withColor(TextColor.parse("GREEN"))
                )
        );
    }

    @Override
    protected boolean postPlacement(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof ChargingStationTile) {
            ChargingStationTile station = (ChargingStationTile) be;
            station.energyStorage.recieveEnergy(stack.getOrCreateTag().getInt("energy"), false);
        }

        return super.postPlacement(pos, world, player, stack, state);
    }
}
