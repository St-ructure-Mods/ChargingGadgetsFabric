package charginggadgets.blockentity;

import charginggadgets.config.CGConfig;
import charginggadgets.init.CGBlockEntities;
import charginggadgets.init.CGContent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import reborncore.api.IToolDrop;
import reborncore.api.blockentity.InventoryProvider;
import reborncore.client.screen.BuiltScreenHandlerProvider;
import reborncore.client.screen.builder.BuiltScreenHandler;
import reborncore.client.screen.builder.ScreenHandlerBuilder;
import reborncore.common.powerSystem.PowerAcceptorBlockEntity;
import reborncore.common.util.RebornInventory;
import team.reborn.energy.Energy;

public class ChargingStationBlockEntity extends PowerAcceptorBlockEntity implements IToolDrop, InventoryProvider, BuiltScreenHandlerProvider {
    private int inventorySize = 1;

    public RebornInventory<ChargingStationBlockEntity> inventory = new RebornInventory<>(inventorySize, "ChargingStationBlockEntity", 64, this);

    public ChargingStationBlockEntity() {
        super(CGBlockEntities.CHARGING_STATION);
    }

    // TilePowerAcceptor
    @Override
    public void tick() {
        super.tick();

        if (world.isClient) {
            return;
        }

        for (int i = 0; i < inventorySize; i++) {
            ItemStack stack = inventory.getStack(i);

            if (Energy.valid(stack)) {
                Energy.of(this)
                        .into(Energy.of(stack))
                        .move();
            }
        }
    }

    @Override
    public double getBaseMaxPower() {
        return CGConfig.chargingStationMaxEnergy;
    }

    @Override
    public boolean canAcceptEnergy(final Direction direction) {
        return true;
    }

    @Override
    public boolean canProvideEnergy(final Direction direction) {
        return direction == null;
    }

    @Override
    public double getBaseMaxOutput() {
        return CGConfig.chargingStationMaxOutput;
    }

    @Override
    public double getBaseMaxInput() {
        return CGConfig.chargingStationMaxInput;
    }

    @Override
    public boolean canBeUpgraded() {
        return false;
    }

    @Override
    public ItemStack getToolDrop(final PlayerEntity entityPlayer) {
        return CGContent.Machine.CHARGING_STATION.getStack();
    }

    @Override
    public RebornInventory<ChargingStationBlockEntity> getInventory() {
        return inventory;
    }

    @Override
    public BuiltScreenHandler createScreenHandler(int syncID, final PlayerEntity player) {
        return new ScreenHandlerBuilder("chargingstation").player(player.inventory).inventory().hotbar().addInventory()
                .blockEntity(this).energySlot(0, 62, 25).energySlot(1, 98, 25).energySlot(2, 62, 45).energySlot(3, 98, 45)
                .energySlot(4, 62, 65).energySlot(5, 98, 65).syncEnergyValue().addInventory().create(this, syncID);
    }
}
