package net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate;

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
public class DeepslateBlastProcessorScreen extends HandledScreen<DeepslateBlastProcessorScreenHandler> {
    private static final Identifier TEXTURE = KlaxonCommon.locate("textures/gui/container/deepslate_blast_processor.png");

    public DeepslateBlastProcessorScreen(DeepslateBlastProcessorScreenHandler handler, PlayerInventory inventory, Text title) {
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
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        updateRecipeDataDisplay(context);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    private void updateRecipeDataDisplay(DrawContext context) {
        String explosionPowerMin = Double.valueOf(handler.explosionPowerMin).toString();
        String explosionPower = Double.valueOf(handler.explosionPower).toString();
        String explosionPowerMax = Double.valueOf(handler.explosionPowerMax).toString();

        context.drawText(textRenderer, explosionPowerMax.substring(0, explosionPowerMax.indexOf('.') + 2), getOffsetX(63), getOffsetY(22), 16777215, false);
        context.drawText(textRenderer, explosionPower.substring(0, explosionPower.indexOf('.') + 2), getOffsetX(63), getOffsetY(40), 16777215, false);
        context.drawText(textRenderer, explosionPowerMin.substring(0, explosionPowerMin.indexOf('.') + 2), getOffsetX(63), getOffsetY(58), 16777215, false);
    }

    private int getOffsetX(int guiX) {
        return x + guiX;
    }

    private int getOffsetY(int guiY) {
        return y + guiY;
    }
}
