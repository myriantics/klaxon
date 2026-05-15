package net.myriantics.klaxon.screen.container;

import net.myriantics.klaxon.screen.BaseKlaxonContainerScreen;

public class Region {
    private final int minX;
    private final int minY;
    private final int maxX;
    private final int maxY;

    public Region(BaseKlaxonContainerScreen<?> screen, int minX, int minY, int maxX, int maxY) {
        this.minX = screen.getOffsetX(minX);
        this.minY = screen.getOffsetY(minY);
        this.maxX = screen.getOffsetX(maxX);
        this.maxY = screen.getOffsetY(maxY);
    }

    public boolean mouseInside(int rawMouseX, int rawMouseY) {
        return rawMouseX >= this.minX && rawMouseX <= this.maxX && rawMouseY >= this.minY && rawMouseY <= this.maxY;
    }

    public int getMinX() {
        return this.minX;
    }

    public int getMinY() {
        return this.minY;
    }
}
