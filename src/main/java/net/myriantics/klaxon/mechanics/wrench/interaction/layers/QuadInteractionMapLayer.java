package net.myriantics.klaxon.mechanics.wrench.interaction.layers;

import net.minecraft.world.phys.Vec2;
import net.myriantics.klaxon.mechanics.wrench.interaction.InteractionMapLayer;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteraction;
import org.jetbrains.annotations.Nullable;

public class QuadInteractionMapLayer implements InteractionMapLayer {

    protected static final int MIN_VALUE = 0;
    protected static final int MAX_VALUE = 16;

    public final int minX;
    public final int minY;
    public final int maxX;
    public final int maxY;

    private final WrenchInteraction interaction;

    public QuadInteractionMapLayer(WrenchInteraction interaction, int minX, int minY, int maxX, int maxY) {
        this.interaction = interaction;
        this.minX = validate(minX);
        this.minY = validate(minY);
        this.maxX = validate(maxX);
        this.maxY = validate(maxY);
        if (this.minX >= this.maxX || this.minY >= this.maxY) {
            throw new IllegalArgumentException("Minimum must be less than maximum");
        }
    }

    public static QuadInteractionMapLayer square(WrenchInteraction interaction, int x, int y, int width) {
        return new QuadInteractionMapLayer(interaction, x, y, x + width, y + width);
    }

    public static QuadInteractionMapLayer fullBlock(WrenchInteraction interaction) {
        return square(interaction, 0, 0, 16);
    }

    @Override
    public @Nullable WrenchInteraction getInteraction(Vec2 faceClickedPos) {
        float clickedX = faceClickedPos.x;
        if (clickedX < this.minX || clickedX > this.maxX) {
            return null;
        }
        float clickedY = faceClickedPos.y;
        if (clickedY < this.minY || clickedX > this.maxY) {
            return null;
        }
        return this.interaction;
    }

    private int validate(int param) throws IllegalArgumentException {
        if (param < 0 || param > 16) {
            throw new IllegalArgumentException("RectangleInteractionMapLayer parameter must be in bounds [" + MIN_VALUE + ", " + MAX_VALUE + "], was [" + param + "]");
        } else {
            return param;
        }
    }
}
