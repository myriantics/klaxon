package net.myriantics.klaxon.registry.render;

import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import net.minecraft.util.Identifier;
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

    public static final Model PIPE_MATRIX_U_BEND_X_POSITIVE = block("pipe_matrix_u_bend/x_positive",
            TextureKey.TOP,
            KlaxonTextureKeys.U_BEND_CURVE,
            KlaxonTextureKeys.U_BEND_BOTTOM,
            KlaxonTextureKeys.U_BEND_SIDE,
            TextureKey.PARTICLE
    );
    public static final Model PIPE_MATRIX_U_BEND_X_NEGATIVE = copyDir("x_negative", PIPE_MATRIX_U_BEND_X_POSITIVE,
            TextureKey.BOTTOM,
            KlaxonTextureKeys.U_BEND_CURVE,
            KlaxonTextureKeys.U_BEND_SIDE,
            KlaxonTextureKeys.U_BEND_SIDE,
            TextureKey.PARTICLE
    );
    public static final Model PIPE_MATRIX_U_BEND_Z_POSITIVE = copyDir("z_positive", PIPE_MATRIX_U_BEND_X_POSITIVE);
    public static final Model PIPE_MATRIX_U_BEND_Z_NEGATIVE = copyDir("z_negative", PIPE_MATRIX_U_BEND_X_NEGATIVE);

    public static final Model NORMAL_NETHER_REACTOR_CORE = block("nether_reactor_core/normal",
            KlaxonTextureKeys.CASING,
            KlaxonTextureKeys.CORE,
            TextureKey.PARTICLE
    );
    public static final Model ROTATED_NETHER_REACTOR_CORE = copyDir("rotated", NORMAL_NETHER_REACTOR_CORE);

    public static Optional<Identifier> id(Model model) {
        return ((ModelAccessor) model).klaxon$getParent();
    }

    private static Model copyDir(String path, Model parent, TextureKey... textureKeys) {
        Optional<Identifier> parentId = id(parent);
        if (parentId.isEmpty()) {
            return new Model(Optional.empty(), Optional.empty());
        }

        String parentPath = parentId.get().getPath();

        int lastParentSlash = parentPath.lastIndexOf('/');
        Identifier modelId = KlaxonCommon.locate(
                lastParentSlash == -1
                        ? path
                        : parentPath.substring(0, lastParentSlash) + '/' + path
        );
        return new Model(Optional.of(modelId), Optional.empty(), textureKeys);
    }

    private static Model copyDir(String path, Model parent) {
        return copyDir(path, parent, ((ModelAccessor)parent).klaxon$getRequiredTextures().toArray(new TextureKey[] {}));
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
