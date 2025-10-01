package net.myriantics.klaxon.registry.render;

import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mixin.minecraft.datagen.ModelAccessor;

import java.util.Optional;

public abstract class KlaxonModels {
    public static final Model GRAPPLE_WINCH_3D_SPOOL_4 = item("grapple_winch_3d/spool_4",
            TextureKey.of("structure"),
            TextureKey.of("spool"),
            TextureKey.of("claw")
    );
    public static final Model GRAPPLE_WINCH_3D_SPOOL_3 = copyTextureMap("grapple_winch_3d/spool_3", GRAPPLE_WINCH_3D_SPOOL_4);
    public static final Model GRAPPLE_WINCH_3D_SPOOL_2 = copyTextureMap("grapple_winch_3d/spool_2", GRAPPLE_WINCH_3D_SPOOL_4);
    public static final Model GRAPPLE_WINCH_3D_SPOOL_1 = copyTextureMap("grapple_winch_3d/spool_1", GRAPPLE_WINCH_3D_SPOOL_4);
    public static final Model GRAPPLE_WINCH_3D_SPOOL_0 = copyTextureMap("grapple_winch_3d/spool_0", GRAPPLE_WINCH_3D_SPOOL_4);

    private static Model item(String path, TextureKey... requiredTextureKeys) {
        return new Model(Optional.of(KlaxonCommon.locate("item/" + path)), Optional.empty(), requiredTextureKeys);
    }

    private static Model copyTextureMap(String path, Model model) {
        return new Model(Optional.of(KlaxonCommon.locate("item/" + path)), Optional.empty(), ((ModelAccessor)model).klaxon$getRequiredTextures().toArray(new TextureKey[]{}));
    }
}
