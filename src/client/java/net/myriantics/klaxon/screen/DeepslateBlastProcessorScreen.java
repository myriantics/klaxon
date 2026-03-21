package net.myriantics.klaxon.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorScreenHandler;

@Environment(EnvType.CLIENT)
public class DeepslateBlastProcessorScreen extends AbstractContainerScreen<DeepslateBlastProcessorScreenHandler> {
    private static final ResourceLocation TEXTURE = KlaxonCommon.locate("textures/gui/container/deepslate_blast_processor.png");

    public DeepslateBlastProcessorScreen(DeepslateBlastProcessorScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        context.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        updateRecipeDataDisplay(context);
        renderTooltip(context, mouseX, mouseY);
    }

    private void updateRecipeDataDisplay(GuiGraphics context) {
        String explosionPowerMin = Double.valueOf(menu.explosionPowerMin).toString();
        String explosionPower = Double.valueOf(menu.explosionPower).toString();
        String explosionPowerMax = Double.valueOf(menu.explosionPowerMax).toString();

        context.drawString(font, explosionPowerMax.substring(0, explosionPowerMax.indexOf('.') + 2), getOffsetX(63), getOffsetY(22), 16777215, false);
        context.drawString(font, explosionPower.substring(0, explosionPower.indexOf('.') + 2), getOffsetX(63), getOffsetY(40), 16777215, false);
        context.drawString(font, explosionPowerMin.substring(0, explosionPowerMin.indexOf('.') + 2), getOffsetX(63), getOffsetY(58), 16777215, false);
    }

    private int getOffsetX(int guiX) {
        return leftPos + guiX;
    }

    private int getOffsetY(int guiY) {
        return topPos + guiY;
    }
}
