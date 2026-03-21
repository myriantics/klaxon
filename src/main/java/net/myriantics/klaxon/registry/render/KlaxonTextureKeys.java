package net.myriantics.klaxon.registry.render;

import net.minecraft.data.models.model.TextureSlot;

public abstract class KlaxonTextureKeys {

    public static final TextureSlot U_BEND_BOTTOM = of("u_bend_bottom");
    public static final TextureSlot U_BEND_SIDE = of("u_bend_side");
    public static final TextureSlot U_BEND_CURVE = of("u_bend_curve");

    public static final TextureSlot CASING = of("casing");
    public static final TextureSlot CORE = of("core");

    private static TextureSlot of(String name) {
        return TextureSlot.create(name);
    }
}
