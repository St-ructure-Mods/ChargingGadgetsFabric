package charginggadgets.blockentity;

import charginggadgets.config.CGConfig;
import charginggadgets.init.CGBlockEntities;
import charginggadgets.init.CGContent;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import reborncore.api.IToolDrop;
import reborncore.api.blockentity.InventoryProvider;
import reborncore.client.screen.BuiltScreenHandlerProvider;
import reborncore.client.screen.builder.BuiltScreenHandler;
import reborncore.client.screen.builder.ScreenHandlerBuilder;
import reborncore.common.blockentity.MachineBaseBlockEntity;
import reborncore.common.blocks.BlockMachineBase;
import reborncore.common.powerSystem.PowerAcceptorBlockEntity;
import reborncore.common.powerSystem.RcEnergyItem;
import reborncore.common.util.RebornInventory;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;
import team.reborn.energy.api.base.SimpleBatteryItem;

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
        Map<Item, Integer> burnMap = AbstractFurnaceBlockEntity.createFuelTimeMap();
        if (burnMap.containsKey(stack.getItem())) {
            return burnMap.get(stack.getItem()) / 4;
        }
        return 0;
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, MachineBaseBlockEntity blockEntity2) {
        super.tick(world, pos, state, blockEntity2);
        if (world.isClient) {
            return;
        }

        tryBurn();
        if (!inventory.getStack(Slots.CHARGE.id).isEmpty()) {
            discharge(Slots.CHARGE.id);
        }
    }

    private void tryCharge() {
        ItemStack chargeSlot = inventory.getStack(Slots.CHARGE.getId());

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
            burnTime = totalBurnTime = ChargingStationBlockEntity.getItemBurnTime(inventory.getStack(Slots.FUEL.getId())) / 32;
            if (burnTime > 0) {
                updateState();
                burnItem = inventory.getStack(Slots.FUEL.getId());
                if (inventory.getStack(Slots.FUEL.getId()).getCount() == 1) {
                    if (inventory.getStack(Slots.FUEL.getId()).getItem() == Items.LAVA_BUCKET || inventory.getStack(Slots.FUEL.getId()).getItem() instanceof BucketItem) {
                        inventory.setStack(Slots.FUEL.getId(), new ItemStack(Items.BUCKET));
                    } else {
                        inventory.setStack(Slots.FUEL.getId(), ItemStack.EMPTY);
                    }
                } else {
                    inventory.shrinkSlot(Slots.FUEL.getId(), 1);
                }
            }
        }

        lastTickBurning = isBurning;
    }

    public void updateState() {
        assert world != null;
        final BlockState BlockStateContainer = world.getBlockState(pos);
        if (BlockStateContainer.getBlock() instanceof final BlockMachineBase blockMachineBase) {
            boolean active = burnTime > 0 && getEnergy() < getMaxStoredPower();
            if (BlockStateContainer.get(BlockMachineBase.ACTIVE) != active) {
                blockMachineBase.setActive(active, world, pos);
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

    @Override
    public BuiltScreenHandler createScreenHandler(int syncID, final PlayerEntity player) {
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
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        setEnergy(tag.getInt("energy"));
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putInt("energy", (int) getEnergy());
    }

    public static int getEnergyFromItemStack(ItemStack stack) {
        NbtCompound tag = stack.getOrCreateNbt();

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
