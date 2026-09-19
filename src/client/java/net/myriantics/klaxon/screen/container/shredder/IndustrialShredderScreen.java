package net.myriantics.klaxon.screen.container.shredder;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.machines.energy.appliances.industrial_shredder.IndustrialShredderMenu;
import net.myriantics.klaxon.screen.BaseKlaxonContainerScreen;

public class IndustrialShredderScreen extends BaseKlaxonContainerScreen<IndustrialShredderMenu> {
    private static final ResourceLocation TEXTURE = KlaxonCommon.locate("textures/gui/container/industrial_shredder.png");

    public IndustrialShredderScreen(IndustrialShredderMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
    }
}
