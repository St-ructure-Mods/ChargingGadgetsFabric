package charginggadgets.client.gui;

import charginggadgets.blockentity.ChargingStationBlockEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import reborncore.client.gui.builder.GuiBase;
import reborncore.client.screen.builder.BuiltScreenHandler;

public class GuiChargeStation  extends GuiBase<BuiltScreenHandler> {

    ChargingStationBlockEntity blockEntity;

    public GuiChargeStation(int syncID, final PlayerEntity player, final ChargingStationBlockEntity blockEntity) {
        super(player, blockEntity, blockEntity.createScreenHandler(syncID, player));
        this.blockEntity = blockEntity;
    }

    @Override
    protected void drawBackground(MatrixStack matrixStack, final float f, final int mouseX, final int mouseY) {
        super.drawBackground(matrixStack, f, mouseX, mouseY);
        final Layer layer = Layer.BACKGROUND;

        drawSlot(matrixStack, 62, 25, layer);
        drawSlot(matrixStack, 98, 25, layer);
        drawSlot(matrixStack, 62, 45, layer);
        drawSlot(matrixStack, 98, 45, layer);
        drawSlot(matrixStack, 62, 65, layer);
        drawSlot(matrixStack, 98, 65, layer);
    }

    @Override
    protected void drawForeground(MatrixStack matrixStack, final int mouseX, final int mouseY) {
        super.drawForeground(matrixStack, mouseX, mouseY);
        final Layer layer = Layer.FOREGROUND;

        builder.drawMultiEnergyBar(matrixStack, this, 81, 28, (int) blockEntity.getEnergy(), (int) blockEntity.getMaxPower(), mouseX, mouseY, 0, layer);
    }
}
