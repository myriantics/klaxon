package net.myriantics.klaxon.registry.render;

import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mixin.minecraft.datagen.ModelAccessor;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;

import java.util.Optional;

public abstract class KlaxonModels {
    private static final Identifier EMPTY = KlaxonCommon.locate("empty");

    public static final Model GRAPPLE_WINCH_3D_SPOOL_4 = item("grapple_winch_3d/spool_4",
            TextureKey.of("structure"),
            TextureKey.of("spool"),
            TextureKey.of("claw")
    );
    public static final Model GRAPPLE_WINCH_3D_SPOOL_3 = copyTextureMap("grapple_winch_3d/spool_3", GRAPPLE_WINCH_3D_SPOOL_4);
    public static final Model GRAPPLE_WINCH_3D_SPOOL_2 = copyTextureMap("grapple_winch_3d/spool_2", GRAPPLE_WINCH_3D_SPOOL_4);
    public static final Model GRAPPLE_WINCH_3D_SPOOL_1 = copyTextureMap("grapple_winch_3d/spool_1", GRAPPLE_WINCH_3D_SPOOL_4);
    public static final Model GRAPPLE_WINCH_3D_SPOOL_0 = copyTextureMap("grapple_winch_3d/spool_0", GRAPPLE_WINCH_3D_SPOOL_4);

    public static final Model PIPE_MATRIX_U_BEND_X = block("pipe_matrix_u_bend_x");
    public static final Model PIPE_MATRIX_U_BEND_Z = block("pipe_matrix_u_bend_z");

    public static Identifier id(Model model) {
        return ((ModelAccessor) model).klaxon$getParent().orElse(EMPTY);
    }

    private static Model block(String path, TextureKey... requiredTextureKeys) {
        return new Model(Optional.of(KlaxonCommon.locate("block/" + path)), Optional.empty(), requiredTextureKeys);
    }

    private static Model item(String path, TextureKey... requiredTextureKeys) {
        return new Model(Optional.of(KlaxonCommon.locate("item/" + path)), Optional.empty(), requiredTextureKeys);
    }

    private static Model copyTextureMap(String path, Model model) {
        return new Model(Optional.of(KlaxonCommon.locate("item/" + path)), Optional.empty(), ((ModelAccessor)model).klaxon$getRequiredTextures().toArray(new TextureKey[]{}));
    }
}
