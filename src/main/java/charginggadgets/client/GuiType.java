package charginggadgets.client;

import charginggadgets.ChargingGadgets;
import charginggadgets.blockentity.ChargingStationBlockEntity;
import charginggadgets.client.gui.GuiChargeStation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import reborncore.RebornCore;
import reborncore.api.blockentity.IMachineGuiHandler;
import reborncore.common.screen.BuiltScreenHandler;
import reborncore.common.screen.BuiltScreenHandlerProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class GuiType<T extends BlockEntity> implements IMachineGuiHandler {
    static final Map<ResourceLocation, GuiType<?>> TYPES = new HashMap<>();

    public static final GuiType<ChargingStationBlockEntity> CHARGING_STATION = register("chargingstation", GuiChargeStation::new);

    private static <T extends BlockEntity> GuiType<T> register(String id, GuiFactory<T> factory) {
        return register(new ResourceLocation(ChargingGadgets.MOD_ID, id), factory);
    }

    public static <T extends BlockEntity> GuiType<T> register(ResourceLocation identifier, GuiFactory<T> factory) {
        if (TYPES.containsKey(identifier)) {
            throw new RuntimeException("Duplicate gui type found");
        }
        GuiType<T> type = new GuiType<>(identifier, factory);
        TYPES.put(identifier, type);
        return type;
    }

    private final ResourceLocation identifier;
    private final MenuType<BuiltScreenHandler> screenHandlerType;

    @Environment(EnvType.CLIENT)
    private GuiType(ResourceLocation identifier, GuiFactory<T> factory) {
        this.identifier = identifier;
        this.screenHandlerType = Registry.register(Registry.MENU, identifier, new ExtendedScreenHandlerType<>(getScreenHandlerFactory()));

        TYPES.put(identifier, this);
    }

    public ResourceLocation getResourceLocation() {
        return identifier;
    }

    public MenuType<BuiltScreenHandler>  getScreenHandlerType() {
        return screenHandlerType;
    }

    private ScreenHandlerRegistry.ExtendedClientHandlerFactory<BuiltScreenHandler> getScreenHandlerFactory() {
        return (syncId, playerInventory, packetByteBuf) -> {
            final BlockEntity blockEntity = playerInventory.player.level.getBlockEntity(packetByteBuf.readBlockPos());
            assert blockEntity != null;
            BuiltScreenHandler screenHandler = ((BuiltScreenHandlerProvider) blockEntity).createScreenHandler(syncId, playerInventory.player);

            //Set the screen handler type, not ideal but works lol
            screenHandler.setType(screenHandlerType);

            return screenHandler;
        };
    }

    @Override
    public void open(Player player, BlockPos pos, Level world) {
        if (!world.isClientSide) {
            //This is awful
            player.openMenu(new ExtendedScreenHandlerFactory() {
                @Override
                public void writeScreenOpeningData(ServerPlayer serverPlayerEntity, FriendlyByteBuf packetByteBuf) {
                    packetByteBuf.writeBlockPos(pos);
                }

                @Override
                public Component getDisplayName() {
                    return new TextComponent("What is this for?");
                }

                @Override
                public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
                    final BlockEntity blockEntity = player.level.getBlockEntity(pos);
                    assert blockEntity != null;
                    BuiltScreenHandler screenHandler = ((BuiltScreenHandlerProvider) blockEntity).createScreenHandler(syncId, player);
                    screenHandler.setType(screenHandlerType);
                    return screenHandler;
                }
            });
        }
    }
}
