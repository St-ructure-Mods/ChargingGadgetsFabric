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
        super.drawBackground(matrixStack, f, mouseX, mouseY);

        RenderSystem.setShaderTexture(0, background);
        drawTexture(matrixStack, getGuiLeft(), getGuiTop(), 0, 0, this.backgroundWidth, 80);

        int maxHeight = 13;
        if (this.blockEntity.totalBurnTime > 0) {
            int remaining = (this.blockEntity.burnTime * maxHeight) / this.blockEntity.totalBurnTime;
            drawTexture(matrixStack, getGuiLeft() + 66, getGuiTop() + 26 + 13 - remaining, 176, 13 - remaining, 14, remaining + 1);
        }

        int maxEnergy = (int) this.blockEntity.getMaxStoredPower(), height = 70;

        if (maxEnergy > 0) {
            int remaining = (int) ((this.blockEntity.getEnergy() * height) / maxEnergy);
            drawTexture(matrixStack, getGuiLeft() + 8, getGuiTop() + 78 - remaining, 176, 84 - remaining, 16, remaining + 1);
        }

    }

    @Override
    protected void drawForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.drawForeground(matrixStack, mouseX, mouseY);
    }
}
