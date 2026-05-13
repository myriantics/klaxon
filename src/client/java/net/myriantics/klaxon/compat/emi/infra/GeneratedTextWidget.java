package net.myriantics.klaxon.compat.emi.infra;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.TextWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.RandomSource;

import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

public class GeneratedTextWidget extends TextWidget {

    private final Function<Random, FormattedCharSequence> function;
    private final Minecraft minecraft = Minecraft.getInstance();
    private FormattedCharSequence text = null;
    private final int unique;
    private long lastGenerate;

    public GeneratedTextWidget(Function<Random, FormattedCharSequence> function, int unique, int x, int y, int color, boolean shadow) {
        super(function.apply(getRandom(0, unique)), x, y, color, shadow);
        this.unique = unique;
        this.function = function;
    }

    @Override
    public void render(GuiGraphics draw, int mouseX, int mouseY, float delta) {
        this.getText();
        draw.pose().pushPose();
        int xOff = this.horizontalAlignment.offset(minecraft.font.width(this.text));
        Alignment var10000 = this.verticalAlignment;
        Objects.requireNonNull(minecraft.font);
        int yOff = var10000.offset(9);
        draw.pose().translate((float)xOff, (float)yOff, 300.0F);
        if (this.shadow) {
            draw.drawString(minecraft.font, this.text, this.x, this.y, this.color, true);
        } else {
            draw.drawString(minecraft.font, this.text, this.x, this.y, this.color, false);
        }

        draw.pose().popPose();
    }

    public FormattedCharSequence getText() {
        long time = System.currentTimeMillis() / 1000L;
        if (this.text == null || time > this.lastGenerate) {
            this.lastGenerate = time;
            this.text = this.function.apply(this.getRandom(time, this.unique));
        }
        return this.text;
    }

    private static Random getRandom(long time, int unique) {
        return new Random((new Random(time ^ (long)unique)).nextInt());
    }
}
