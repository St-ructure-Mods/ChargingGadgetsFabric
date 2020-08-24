package charginggadgets.blockentity;

import charginggadgets.config.CGConfig;
import charginggadgets.init.CGBlockEntities;
import charginggadgets.init.CGContent;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Direction;
import reborncore.api.IToolDrop;
import reborncore.api.blockentity.InventoryProvider;
import reborncore.client.screen.BuiltScreenHandlerProvider;
import reborncore.client.screen.builder.BuiltScreenHandler;
import reborncore.client.screen.builder.ScreenHandlerBuilder;
import reborncore.common.blocks.BlockMachineBase;
import reborncore.common.powerSystem.PowerAcceptorBlockEntity;
import reborncore.common.util.RebornInventory;

import java.util.Map;

public class ChargingStationBlockEntity extends PowerAcceptorBlockEntity implements IToolDrop, InventoryProvider, BuiltScreenHandlerProvider {
    private final int inventorySize = 3;
    public RebornInventory<ChargingStationBlockEntity> inventory = new RebornInventory<>(inventorySize, "ChargingStationBlockEntity", 64, this);
    public int fuelSlot = 0;
    public int burnTime;
    public int totalBurnTime = 0;
    public boolean isBurning;
    public boolean lastTickBurning;
    ItemStack burnItem;

    public ChargingStationBlockEntity() {
        super(CGBlockEntities.CHARGING_STATION);
    }

    public static int getItemBurnTime(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        Map<Item, Integer> burnMap = AbstractFurnaceBlockEntity.createFuelTimeMap();
        if (burnMap.containsKey(stack.getItem())) {
            return burnMap.get(stack.getItem()) / 4;
        }
        return 0;
    }

    @Override
    public void tick() {
        super.tick();
        if (world.isClient) {
            return;
        }
        discharge(1);
        if (getEnergy() < getMaxPower()) {
            if (burnTime > 0) {
                burnTime--;
                addEnergy(1000);
                isBurning = true;
            }
        } else {
            isBurning = false;
            updateState();
        }

        if (burnTime == 0) {
            updateState();
            burnTime = totalBurnTime = ChargingStationBlockEntity.getItemBurnTime(inventory.getStack(fuelSlot)) / 32;
            if (burnTime > 0) {
                updateState();
                burnItem = inventory.getStack(fuelSlot);
                if (inventory.getStack(fuelSlot).getCount() == 1) {
                    if (inventory.getStack(fuelSlot).getItem() == Items.LAVA_BUCKET || inventory.getStack(fuelSlot).getItem() instanceof BucketItem) {
                        inventory.setStack(fuelSlot, new ItemStack(Items.BUCKET));
                    } else {
                        inventory.setStack(fuelSlot, ItemStack.EMPTY);
                    }
                } else {
                    inventory.shrinkSlot(fuelSlot, 1);
                }
            }
        }

        lastTickBurning = isBurning;
    }

    public void updateState() {
        final BlockState BlockStateContainer = world.getBlockState(pos);
        if (BlockStateContainer.getBlock() instanceof BlockMachineBase) {
            final BlockMachineBase blockMachineBase = (BlockMachineBase) BlockStateContainer.getBlock();
            boolean active = burnTime > 0 && getEnergy() < getMaxPower();
            if (BlockStateContainer.get(BlockMachineBase.ACTIVE) != active) {
                blockMachineBase.setActive(active, world, pos);
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
        return false;
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
    public ItemStack getToolDrop(final PlayerEntity entityPlayer) {
        return CGContent.Machine.CHARGING_STATION.getStack();
    }

    @Override
    public RebornInventory<ChargingStationBlockEntity> getInventory() {
        return inventory;
    }

    public int getBurnTime() {
        return burnTime;
    }

    public void setBurnTime(final int burnTime) {
        this.burnTime = burnTime;
    }

    public int getTotalBurnTime() {
        return totalBurnTime;
    }

    public void setTotalBurnTime(final int totalBurnTime) {
        this.totalBurnTime = totalBurnTime;
    }

    public int getScaledBurnTime(final int i) {
        return (int) ((float) burnTime / (float) totalBurnTime * i);
    }

    @Override
    public BuiltScreenHandler createScreenHandler(int syncID, final PlayerEntity player) {
        return new ScreenHandlerBuilder("chargingstation")
                .player(player.inventory).inventory().hotbar().addInventory()
                .blockEntity(this)
                .fuelSlot(0, 65, 43)
                .slot(1, 119, 43)
                .energySlot(2, 7, 72)
                .syncEnergyValue()
                .sync(this::getBurnTime, this::setBurnTime)
                .sync(this::getTotalBurnTime, this::setTotalBurnTime)
                .addInventory().create(this, syncID);
    }

    @Override
	public boolean canBeUpgraded() {
		return false;
	}

	@Override
	public boolean hasSlotConfig() {
		return false;
	}
}
