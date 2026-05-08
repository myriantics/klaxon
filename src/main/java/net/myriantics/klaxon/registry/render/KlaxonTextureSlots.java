package net.myriantics.klaxon.registry.render;

import net.minecraft.data.models.model.TextureSlot;

public abstract class KlaxonTextureSlots {

    public static final TextureSlot U_BEND_BOTTOM = of("u_bend_bottom");
    public static final TextureSlot U_BEND_SIDE = of("u_bend_side");
    public static final TextureSlot U_BEND_CURVE = of("u_bend_curve");

    public static final TextureSlot TOP_LAYER_0 = of("top_layer_0");
    public static final TextureSlot TOP_LAYER_1 = of("top_layer_1");
    public static final TextureSlot SIDE_LAYER_0 = of("side_layer_0");
    public static final TextureSlot SIDE_LAYER_1 = of("side_layer_1");
    public static final TextureSlot BOTTOM_LAYER_0 = of("bottom_layer_0");
    public static final TextureSlot BOTTOM_LAYER_1 = of("bottom_layer_1");

    public static final TextureSlot CASING = of("casing");
    public static final TextureSlot CORE = of("core");

    private static TextureSlot of(String name) {
        return TextureSlot.create(name);
    }
}
