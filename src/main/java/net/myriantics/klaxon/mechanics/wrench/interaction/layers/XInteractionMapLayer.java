package net.myriantics.klaxon.mechanics.wrench.interaction.layers;

import net.minecraft.world.phys.Vec2;
import net.myriantics.klaxon.mechanics.wrench.WrenchActionType;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteraction;
import org.jetbrains.annotations.Nullable;

public class XInteractionMapLayer extends QuadInteractionMapLayer {

    private final WrenchInteraction top;
    private final WrenchInteraction bottom;
    private final WrenchInteraction left;
    private final WrenchInteraction right;

    private final float slope;
    private final float middleX;
    private final float middleY;

    public XInteractionMapLayer(WrenchInteraction top, WrenchInteraction bottom, WrenchInteraction left, WrenchInteraction right, int minX, int minY, int maxX, int maxY) {
        super(top, minX, minY, maxX, maxY);
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
        this.slope = (float) (maxY - minY) / (maxX - minX);
        this.middleX = minX + (float) (maxX - minX) / 2;
        this.middleY = minY + (float) (maxY - minY) / 2;
    }

    public static XInteractionMapLayer square(WrenchInteraction top, WrenchInteraction bottom, WrenchInteraction left, WrenchInteraction right, int x, int y, int width) {
        return new XInteractionMapLayer(top, bottom, left, right, x, y, x + width, y + width);
    }

    public static XInteractionMapLayer fullBlock(WrenchInteraction top, WrenchInteraction bottom, WrenchInteraction left, WrenchInteraction right) {
        return square(top, bottom, left, right, 0, 0, 16);
    }

    @Override
    public @Nullable WrenchInteraction getInteraction(Vec2 faceClickedPos) {
        float clickedX = faceClickedPos.x;
        float clickedY = faceClickedPos.y;

        float posSlopeX = this.posSlopeXatY(clickedY);
        float posSlopeY = this.posSlopeYatX(clickedX);
        float negSlopeX = this.negSlopeXatY(clickedY);
        float negSlopeY = this.negSlopeYatX(clickedX);

        if (clickedY > this.middleY && clickedX > negSlopeX && clickedX < posSlopeX) {
            return this.top;
        } else if (clickedY < this.middleY && clickedX > negSlopeX && clickedX < posSlopeX) {
            return this.bottom;
        } else if (clickedX > this.middleX && clickedY > negSlopeY && clickedY < posSlopeY) {
            return this.right;
        } else if (clickedX < this.middleX && clickedY > negSlopeY && clickedY < posSlopeY) {
            return this.left;
        } else {
            return null;
        }
    }

    private float posSlopeXatY(float y) {
        return ((y + this.minY) / this.slope) - minX;
    }

    private float posSlopeYatX(float x) {
        return (this.slope * (x + this.minX)) - this.minY;
    }

    private float negSlopeXatY(float y) {
        return ((y + this.minY + MAX_VALUE) / -this.slope) - minX;
    }

    private float negSlopeYatX(float x) {
        return (-this.slope * (x + this.minX + MAX_VALUE)) - this.minY;
    }
}
