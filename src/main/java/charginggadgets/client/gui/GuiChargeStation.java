package charginggadgets.client.gui;

import charginggadgets.blockentity.ChargingStationBlockEntity;
import charginggadgets.ChargingGadgets;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import reborncore.client.gui.builder.GuiBase;
import reborncore.client.screen.builder.BuiltScreenHandler;

public class GuiChargeStation extends GuiBase<BuiltScreenHandler> {
    private static final Identifier background = new Identifier(ChargingGadgets.MOD_ID, "textures/gui/charging_station.png");

    ChargingStationBlockEntity blockEntity;

    public GuiChargeStation(int syncID, final PlayerEntity player, final ChargingStationBlockEntity blockEntity) {
        super(player, blockEntity, blockEntity.createScreenHandler(syncID, player));
        this.blockEntity = blockEntity;
    }



    @Override
    protected void drawBackground(MatrixStack matrixStack, final float f, final int mouseX, final int mouseY) {
        RenderSystem.color4f(1, 1, 1, 1);
        getMinecraft().getTextureManager().bindTexture(background);
        drawTexture(matrixStack, getGuiLeft(), getGuiLeft(), 0, 0, this.backgroundWidth, this.backgroundHeight);

        int maxHeight = 13;
        if (this.blockEntity.getMaxBurn() > 0) {
            int remaining = (this.blockEntity.getRemaining() * maxHeight) / this.blockEntity.getMaxBurn();
            drawTexture(matrixStack, getGuiLeft() + 66, getGuiTop() + 26 + 13 - remaining, 176, 13 - remaining, 14, remaining + 1);
        }
    }

    @Override
    protected void drawForeground(MatrixStack matrixStack, final int mouseX, final int mouseY) {
        super.drawForeground(matrixStack, mouseX, mouseY);
        final Layer layer = Layer.FOREGROUND;

        builder.drawMultiEnergyBar(matrixStack, this, 81, 28, (int) blockEntity.getEnergy(), (int) blockEntity.getMaxPower(), mouseX, mouseY, 0, layer);
    }
}
