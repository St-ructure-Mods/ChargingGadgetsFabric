package charginggadgets.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import reborncore.common.screen.BuiltScreenHandler;

@Environment(EnvType.CLIENT)
public interface GuiFactory<T extends BlockEntity> extends ScreenRegistry.Factory<BuiltScreenHandler, AbstractContainerScreen<BuiltScreenHandler>> {
    AbstractContainerScreen<?> create(int syncId, Player playerEntity, T blockEntity);

    @Override
    default AbstractContainerScreen create(BuiltScreenHandler builtScreenHandler, Inventory playerInventory, Component text) {
        Player playerEntity = playerInventory.player;
        //noinspection unchecked
        T blockEntity = (T) builtScreenHandler.getBlockEntity();
        return create(builtScreenHandler.containerId, playerEntity, blockEntity);
    }
}