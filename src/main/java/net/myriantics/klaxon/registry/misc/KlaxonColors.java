package net.myriantics.klaxon.registry.misc;

import java.awt.*;

public abstract class KlaxonColors {

    public static Color STEEL_LIGHT = registerHex("#aeadc2");
    public static Color STEEL_MEDIUM = registerHex("#9d98a4");
    public static Color STEEL_DARK = registerHex("#625964");

    private static Color registerHex(String hexCode) {
        Color color = Color.decode(hexCode);
        return color;
    }

    public static float[] toHSBArray(Color color) {
        return Color.RGBtoHSB(
                color.getRed(),
                color.getGreen(),
                color.getBlue(),
                new float[3]
        );
    }
}
