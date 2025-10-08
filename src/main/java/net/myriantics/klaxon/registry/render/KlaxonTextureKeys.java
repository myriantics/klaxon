package net.myriantics.klaxon.registry.render;

import net.minecraft.data.client.TextureKey;

public abstract class KlaxonTextureKeys {

    public static final TextureKey U_BEND_BOTTOM = of("u_bend_bottom");
    public static final TextureKey U_BEND_SIDE = of("u_bend_side");
    public static final TextureKey U_BEND_CURVE = of("u_bend_curve");

    private static TextureKey of(String name) {
        return TextureKey.of(name);
    }
}
