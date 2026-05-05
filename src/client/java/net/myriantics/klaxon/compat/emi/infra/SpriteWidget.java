package net.myriantics.klaxon.compat.emi.infra;

import dev.emi.emi.api.widget.TextureWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class SpriteWidget extends TextureWidget {
    public SpriteWidget(ResourceLocation texture, int x, int y, int width, int height, int u, int v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        super(texture, x, y, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
    }

    public SpriteWidget(ResourceLocation texture, int x, int y, int width, int height, int u, int v) {
        super(texture, x, y, width, height, u, v);
    }

    @Override
    public void render(GuiGraphics draw, int mouseX, int mouseY, float delta) {
        draw.blitSprite(this.texture, this.textureWidth, this.textureHeight, this.u, this.v, this.x, this.y, 0, this.regionWidth, this.regionHeight);
    }
}
