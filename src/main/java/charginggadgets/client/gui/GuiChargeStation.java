package charginggadgets.client.gui;

import charginggadgets.blockentity.ChargingStationBlockEntity;
import charginggadgets.ChargingGadgets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import reborncore.client.gui.builder.GuiBase;
import reborncore.common.screen.BuiltScreenHandler;

public class GuiChargeStation extends GuiBase<BuiltScreenHandler> {
    private static final ResourceLocation background = new ResourceLocation(ChargingGadgets.MOD_ID, "textures/gui/charging_station.png");

    ChargingStationBlockEntity blockEntity;

    public GuiChargeStation(int syncID, final Player player, final ChargingStationBlockEntity blockEntity) {
        super(player, blockEntity, blockEntity.createScreenHandler(syncID, player));
        this.blockEntity = blockEntity;
    }

    @Override
    protected void renderBg(PoseStack matrixStack, final float f, final int mouseX, final int mouseY) {
        super.renderBg(matrixStack, f, mouseX, mouseY);

        RenderSystem.setShaderTexture(0, background);
        blit(matrixStack, getGuiLeft(), getGuiTop(), 0, 0, this.imageWidth, 80);

        int maxHeight = 13;
        if (this.blockEntity.totalBurnTime > 0) {
            int remaining = (this.blockEntity.burnTime * maxHeight) / this.blockEntity.totalBurnTime;
            blit(matrixStack, getGuiLeft() + 66, getGuiTop() + 26 + 13 - remaining, 176, 13 - remaining, 14, remaining + 1);
        }

        int maxEnergy = (int) this.blockEntity.getMaxStoredPower(), height = 70;

        if (maxEnergy > 0) {
            int remaining = (int) ((this.blockEntity.getEnergy() * height) / maxEnergy);
            blit(matrixStack, getGuiLeft() + 8, getGuiTop() + 78 - remaining, 176, 84 - remaining, 16, remaining + 1);
        }

    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        super.renderLabels(matrixStack, mouseX, mouseY);
    }
}
