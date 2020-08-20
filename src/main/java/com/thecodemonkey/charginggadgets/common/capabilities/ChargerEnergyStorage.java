package com.thecodemonkey.charginggadgets.common.capabilities;

import com.thecodemonkey.charginggadgets.common.tiles.ChargingStationTile;
import net.minecraft.nbt.CompoundTag;

public class ChargerEnergyStorage {
    private static final String KEY = "energy";
    private int energy;
    private int capacity;
    private int maxInOut = 1000000;
    private ChargingStationTile tile;

    public ChargerEnergyStorage(ChargingStationTile tile, int energy, int capacity) {
        this.energy = energy;
        this.capacity = capacity;
        this.tile = tile;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(KEY, this.energy);
        return tag;
    }

    public void deserializeNBT(CompoundTag nbt) {
        this.energy = nbt.getInt(KEY);
    }

    public int recieveEnergy(int maxRecieve, boolean simulate) {
        int energyReceived = Math.min(capacity - energy, Math.min(this.maxInOut, maxRecieve));

        if (!simulate) {
            energy += energyReceived;
            this.tile.markDirty();
        }

        return energyReceived;
    }

    public int consumeEnergy(int maxExtract, boolean simulate) {
        int energyExtracted = Math.min(energy, Math.min(this.maxInOut, maxExtract));

        if (!simulate)
            energy -= energyExtracted;

        return energyExtracted;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public int getEnergyStored() {
        return this.energy;
    }

    public int getMaxEnergyStored() {
        return this.capacity;
    }

    public boolean canExtract() {
        return false;
    }

    public boolean canReceive() {
        return true;
    }

    public String toString() {
        return "ChargerEnergyStorage{" +
                "energy=" + energy +
                ", capacity=" + capacity +
                ", maxInOut=" + maxInOut +
                '}';
    }
}
