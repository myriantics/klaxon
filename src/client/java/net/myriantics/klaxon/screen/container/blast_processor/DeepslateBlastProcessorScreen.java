package net.myriantics.klaxon.screen.container.blast_processor;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.player.Inventory;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorMenu;
import net.myriantics.klaxon.screen.container.Region;

public class DeepslateBlastProcessorScreen extends AbstractBlastProcessorScreen<DeepslateBlastProcessorMenu> {
    private static final ResourceLocation TEXTURE = KlaxonCommon.locate("textures/gui/container/deepslate_blast_processor.png");

    private Region explosionPowerRegion;
    private Region explosionPowerMinRegion;
    private Region explosionPowerMaxRegion;

    public DeepslateBlastProcessorScreen(DeepslateBlastProcessorMenu handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.explosionPowerRegion = new Region(this, 13, 37, 36, 48);
        this.explosionPowerMinRegion = new Region(this, 79, 58, 102, 69);
        this.explosionPowerMaxRegion = new Region(this, 79, 16, 102, 27);
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
        renderRecipeDataDisplay(context);
        renderTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        super.renderTooltip(guiGraphics, x, y);
        if (this.hoveredSlot == null) {
            String hoverText;
            if (this.explosionPowerRegion.mouseInside(x, y)) {
                hoverText = this.explosionPower;
            } else if (this.explosionPowerMinRegion.mouseInside(x, y)) {
                hoverText = this.explosionPowerMin;
            } else if (this.explosionPowerMaxRegion.mouseInside(x, y)) {
                hoverText = this.explosionPowerMax;
            } else {
                hoverText = null;
            }
            if (hoverText != null) {
                guiGraphics.renderTooltip(this.font, Component.literal(hoverText), x, y);
            }
        }
    }

    private void renderRecipeDataDisplay(GuiGraphics context) {
        context.drawString(font, truncatedExplosionPowerMax, explosionPowerMaxRegion.getMinX() + 2, explosionPowerMaxRegion.getMinY() + 2, CommonColors.WHITE, true);
        context.drawString(font, truncatedExplosionPower, explosionPowerRegion.getMinX() + 2, explosionPowerRegion.getMinY() + 2, CommonColors.WHITE, true);
        context.drawString(font, truncatedExplosionPowerMin, explosionPowerMinRegion.getMinX() + 2, explosionPowerMinRegion.getMinY() + 2, CommonColors.WHITE, true);
    }
}
