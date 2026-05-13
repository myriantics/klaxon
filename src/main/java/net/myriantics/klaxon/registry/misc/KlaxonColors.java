package net.myriantics.klaxon.registry.misc;

import java.awt.*;

public abstract class KlaxonColors {

    public static Color STEEL_LIGHT = of(0xaeadc2);
    public static Color STEEL_MEDIUM = of(0x9d98a4);
    public static Color STEEL_DARK = of(0x625964);
    public static final Color ORANGE = of(0xFF5C00);
    public static final Color PURPLE = of(0x800080);
    public static final Color FIREWORK_ROCKET_RED = of(0x992929);
    public static final Color FIREWORK_STAR_GREY = of(0x727272);
    public static final Color GLOWSTONE_YELLOW = of(0xFFBC5E);
    public static final Color BED_OAK = of(0x7E6237);
    public static final Color END_CRYSTAL_PURPLE = of(0xBE95D4);
    public static final Color DRAGONS_BREATH_PURPLE = of(0xAC2C7B);
    public static final Color WIND_CHARGE_BLUE = of(0xBDC9FF);
    public static final Color FIERY_ORANGE = of(0xEEAC18);
    public static final Color TNT_RED = of(0xB11527);
    public static final Color TNT_MINECART_RED = of(0xDB441A);
    public static final Color CREEPER_GREEN = of(0x00A500);

    private static Color of(int color) {
        return new Color(color);
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
