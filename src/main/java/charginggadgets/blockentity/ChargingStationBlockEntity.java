package charginggadgets.blockentity;

import charginggadgets.config.CGConfig;
import charginggadgets.init.CGBlockEntities;
import charginggadgets.init.CGContent;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import reborncore.api.IToolDrop;
import reborncore.api.blockentity.InventoryProvider;
import reborncore.client.screen.builder.ScreenHandlerBuilder;
import reborncore.common.blockentity.MachineBaseBlockEntity;
import reborncore.common.blocks.BlockMachineBase;
import reborncore.common.powerSystem.PowerAcceptorBlockEntity;
import reborncore.common.screen.BuiltScreenHandler;
import reborncore.common.screen.BuiltScreenHandlerProvider;
import reborncore.common.util.RebornInventory;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;

import java.util.Map;

public class ChargingStationBlockEntity extends PowerAcceptorBlockEntity implements IToolDrop, InventoryProvider, BuiltScreenHandlerProvider {
    private final int inventorySize = 2;

    public RebornInventory<ChargingStationBlockEntity> inventory;

    public int burnTime;
    public int totalBurnTime = 0;
    public boolean isBurning;
    public boolean lastTickBurning;
    ItemStack burnItem;

    public enum Slots {
        FUEL(0),
        CHARGE(1);

        private final int id;

        Slots(int number) {
            id = number;
        }

        public int getId() {
            return id;
        }
    }

    public ChargingStationBlockEntity(BlockPos pos, BlockState state) {
        super(CGBlockEntities.CHARGING_STATION, pos, state);
        inventory = new RebornInventory<>(inventorySize, "ChargingStationBlockEntity", 64, this);
    }

    public static int getItemBurnTime(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        Map<Item, Integer> burnMap = AbstractFurnaceBlockEntity.getFuel();
        if (burnMap.containsKey(stack.getItem())) {
            return burnMap.get(stack.getItem()) / 4;
        }
        return 0;
    }

    @Override
    public void tick(Level world, BlockPos pos, BlockState state, MachineBaseBlockEntity blockEntity2) {
        super.tick(world, pos, state, blockEntity2);
        if (world.isClientSide) {
            return;
        }

        tryBurn();
        if (!inventory.getItem(Slots.CHARGE.id).isEmpty()) {
            discharge(Slots.CHARGE.id);
        }
    }

    private void tryCharge() {
        ItemStack chargeSlot = inventory.getItem(Slots.CHARGE.getId());

        Item item = chargeSlot.getItem();

        if (!chargeSlot.isEmpty() && EnergyStorageUtil.isEnergyStorage(chargeSlot)) {
            EnergyStorageUtil.move(
                    getSideEnergyStorage(null),
                    ContainerItemContext.ofSingleSlot(InventoryStorage.of(inventory, null).getSlot(Slots.CHARGE.getId())).find(EnergyStorage.ITEM),
                    Long.MAX_VALUE,
                    null
            );
        }
    }

    private void tryBurn() {
        if (getEnergy() < getMaxStoredPower()) {
            if (burnTime > 0) {
                burnTime--;
                addEnergy(500000);
                isBurning = true;
            }
        } else {
            isBurning = false;
            updateState();
        }

        if (burnTime == 0) {
            updateState();
            burnTime = totalBurnTime = ChargingStationBlockEntity.getItemBurnTime(inventory.getItem(Slots.FUEL.getId())) / 32;
            if (burnTime > 0) {
                updateState();
                burnItem = inventory.getItem(Slots.FUEL.getId());
                if (inventory.getItem(Slots.FUEL.getId()).getCount() == 1) {
                    if (inventory.getItem(Slots.FUEL.getId()).getItem() == Items.LAVA_BUCKET || inventory.getItem(Slots.FUEL.getId()).getItem() instanceof BucketItem) {
                        inventory.setItem(Slots.FUEL.getId(), new ItemStack(Items.BUCKET));
                    } else {
                        inventory.setItem(Slots.FUEL.getId(), ItemStack.EMPTY);
                    }
                } else {
                    inventory.shrinkSlot(Slots.FUEL.getId(), 1);
                }
            }
        }

        lastTickBurning = isBurning;
    }

    public void updateState() {
        assert level != null;
        final BlockState BlockStateContainer = level.getBlockState(worldPosition);
        if (BlockStateContainer.getBlock() instanceof final BlockMachineBase blockMachineBase) {
            boolean active = burnTime > 0 && getEnergy() < getMaxStoredPower();
            if (BlockStateContainer.getValue(BlockMachineBase.ACTIVE) != active) {
                blockMachineBase.setActive(active, level, worldPosition);
            }
        }
    }

    @Override
    public long getBaseMaxPower() {
        return CGConfig.chargingStationMaxEnergy;
    }

    @Override
    public boolean canAcceptEnergy(final Direction direction) {
        return false;
    }

    @Override
    public boolean canProvideEnergy(final Direction direction) {
        return true;
    }

    @Override
    public long getBaseMaxOutput() {
        return CGConfig.chargingStationMaxOutput;
    }

    @Override
    public long getBaseMaxInput() {
        return CGConfig.chargingStationMaxInput;
    }

    @Override
    public ItemStack getToolDrop(final Player entityPlayer) {
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

    @Override
    public BuiltScreenHandler createScreenHandler(int syncID, final Player player) {
        return new ScreenHandlerBuilder("charging_station")
                .player(player.getInventory()).inventory().hotbar().addInventory()
                .blockEntity(this)
                .fuelSlot(0, 65, 43)
                .slot(1, 119, 43)
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

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        setEnergy(tag.getInt("energy"));
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("energy", (int) getEnergy());
    }

    public static int getEnergyFromItemStack(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();

        int energy = 0;
        if (tag.contains("energy", NbtType.INT)) {
            energy = tag.getInt("color");
        } else if (tag.contains("BlockEntityTag") && tag.getCompound("BlockEntityTag").contains("energy", NbtType.INT)) {
            energy = tag.getCompound("BlockEntityTag").getInt("energy");
        } else if (tag.contains("energy", NbtType.STRING)) {
            try {
                energy = Integer.parseInt(tag.getString("energy"));
            } catch (NumberFormatException ignored) {}
        }

        return energy;
    }
}
