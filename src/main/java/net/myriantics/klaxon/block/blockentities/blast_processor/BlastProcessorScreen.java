package net.myriantics.klaxon.block.blockentities.blast_processor;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;

@Environment(EnvType.CLIENT)
public class BlastProcessorScreen extends HandledScreen<BlastProcessorScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(KlaxonCommon.MOD_ID, "textures/gui/container/deepslate_blast_processor.png");

    public BlastProcessorScreen(BlastProcessorScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        updateRecipeDataDisplay(context);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    private void updateRecipeDataDisplay(DrawContext context) {
        context.drawText(textRenderer, "" + handler.explosionPowerMax, getOffsetX(63), getOffsetY(22), 16777215, false);
        context.drawText(textRenderer, "" + handler.explosionPower, getOffsetX(63), getOffsetY(40), 16777215, false);
        context.drawText(textRenderer, "" + handler.explosionPowerMin, getOffsetX(63), getOffsetY(58), 16777215, false);
        context.drawText(textRenderer, handler.outputState.toString(), getOffsetX(90), getOffsetY(73), 4210752, false);
    }

    private int getOffsetX(int guiX) {
        return x + guiX;
    }

    private int getOffsetY(int guiY) {
        return y + guiY;
    }
}
