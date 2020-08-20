package com.thecodemonkey.charginggadgets.common.tiles;

import com.thecodemonkey.charginggadgets.common.ModConfig;
import com.thecodemonkey.charginggadgets.common.capabilities.ChargerEnergyStorage;
import com.thecodemonkey.charginggadgets.common.container.ChargingStationContainer;
import com.thecodemonkey.charginggadgets.common.interfaces.IIntArray;
import com.thecodemonkey.charginggadgets.common.inventory.ImplementedInventory;
import com.thecodemonkey.charginggadgets.common.registry.BlocksRegistry;
import jdk.internal.jline.internal.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.collection.DefaultedList;

import java.util.Optional;

public class ChargingStationTile extends BlockEntity implements ImplementedInventory, NamedScreenHandlerFactory {
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(2, ItemStack.EMPTY);

    public enum Slots {
        FUEL(0),
        CHARGE(1);

        int id;

        Slots(int number) {
            id = number;
        }

        public int getId() {
            return id;
        }
    }

    private int counter = 0;
    private int maxBurn = 0;

    public ChargerEnergyStorage energyStorage;
    private Optional<ChargerEnergyStorage> energy;

    public final IIntArray chargingStationData = new IIntArray() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0:
                    return ChargingStationTile.this.energyStorage.getEnergyStored() / 32;
                case 1:
                    return ChargingStationTile.this.energyStorage.getMaxEnergyStored() / 32;
                case 2:
                    return ChargingStationTile.this.counter;
                case 3:
                    return ChargingStationTile.this.maxBurn;
                default:
                    throw new IllegalArgumentException("Invalid index: " + index);
            }
        }

        @Override
        public void set(int index, int value) {
            throw new IllegalStateException("Cannot set values through IIntArray");
        }

        @Override
        public int size() {
            return 4;
        }
    };

    public ChargingStationTile() {
        super(BlocksRegistry.CHARGING_STATION_TILE);
        this.energyStorage = new ChargerEnergyStorage(this, 0, ModConfig.chargerMaxPower);
        this.energy = Optional.of(this.energyStorage);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        assert world != null;
        return new ChargingStationContainer(this, this.chargingStationData, syncId, inv, 2);
    }

//    @Override
//    public DefaultedList<ItemStack> getItems() {
//        return items;
//    }
//
//    @Override
//    public void fromTag(BlockState state, CompoundTag tag) {
//        super.fromTag(state, tag);
//        Inventories.fromTag(tag,items);
//    }
//
//    @Override
//    public CompoundTag toTag(CompoundTag tag) {
//        Inventories.toTag(tag,items);
//        return super.toTag(tag);
//    }
}
