package net.myriantics.klaxon.mechanics.wrench;

import net.minecraft.util.CommonColors;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteractionMap;

public class WrenchActionType {
    private final int color;

    private WrenchActionType(int color) {
        this.color = color;
    }

    public static WrenchActionType of(int color) {
        return new WrenchActionType(color);
    }

    public static WrenchActionType colorless() {
        return of(0);
    }

    public static WrenchActionType red() {
        return of(0xFF0000);
    }

    public static WrenchActionType green() {
        return of(0x00FF00);
    }

    public static WrenchActionType blue() {
        return of(0x0000FF);
    }

    public static WrenchActionType orange() {
        return of(0xFF8000);
    }
}
