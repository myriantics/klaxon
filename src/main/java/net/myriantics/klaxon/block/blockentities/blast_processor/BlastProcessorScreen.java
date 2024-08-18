package net.myriantics.klaxon.block.blockentities.blast_processor;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.events.BlastProcessorScreenDataSyncCallback;
import net.myriantics.klaxon.recipes.blast_processing.BlastProcessingInator;
import net.myriantics.klaxon.recipes.blast_processing.BlastProcessorOutputState;
import net.myriantics.klaxon.util.ItemExplosionPowerHelper;

import static net.myriantics.klaxon.block.blockentities.blast_processor.BlastProcessorBlockEntity.CATALYST_INDEX;

@Environment(EnvType.CLIENT)
public class BlastProcessorScreen extends HandledScreen<BlastProcessorScreenHandler>  implements BlastProcessorScreenDataSyncCallback {
    private static final Identifier TEXTURE = new Identifier("klaxon", "textures/gui/container/deepslate_blast_processor.png");

    private double explosionPower;
    private double explosionPowerMin;
    private double explosionPowerMax;
    private boolean producesFire;
    private boolean requiresFire;
    private BlastProcessorOutputState outputState;

    public BlastProcessorScreen(BlastProcessorScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.explosionPower = 0.0;
        this.explosionPowerMin = 0.0;
        this.explosionPowerMax = 0.0;
        this.producesFire = false;
        this.requiresFire = false;
        this.outputState = BlastProcessorOutputState.MISSING_RECIPE;
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
        context.drawText(textRenderer, "" + explosionPower, 0, 0, 16777215, false);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();

        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }

    @Override
    public void receivePowerData(double explosionPower, double explosionPowerMin, double explosionPowerMax, boolean producesFire, boolean requiresFire, BlastProcessorOutputState outputState) {
        this.explosionPower = explosionPower;
        this.explosionPowerMin = explosionPowerMin;
        this.explosionPowerMax = explosionPowerMax;
        this.producesFire = producesFire;
        this.requiresFire = requiresFire;
    }
}
