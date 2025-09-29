package net.myriantics.klaxon.registry.render;

import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import net.myriantics.klaxon.KlaxonCommon;

import java.util.Optional;

public abstract class KlaxonModels {
    public static final TextureKey[] GRAPPLE_WINCH_3D_TEXTURE_KEYS = new TextureKey[] {
            TextureKey.of("structure"),
            TextureKey.of("spool"),
            TextureKey.of("claw"),
            TextureKey.of("particle")
    };
    public static final Model GRAPPLE_WINCH_3D = item("grapple_winch_3d_parent",
            TextureKey.of("structure"),
            TextureKey.of("spool"),
            TextureKey.of("claw"),
            TextureKey.of("particle")
    );

    private static Model item(String path, TextureKey... requiredTextureKeys) {
        return new Model(Optional.of(KlaxonCommon.locate("item/" + path)), Optional.empty(), requiredTextureKeys);
    }
}
