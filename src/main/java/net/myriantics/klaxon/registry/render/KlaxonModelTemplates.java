package net.myriantics.klaxon.registry.render;

import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mixin.minecraft.datagen.ModelTemplateAccessor;

import java.util.Optional;

public abstract class KlaxonModelTemplates {

    public static final ModelTemplate GRAPPLE_WINCH_3D_SPOOL_6 = item("grapple_winch_3d/spool_6",
            TextureSlot.create("structure"),
            TextureSlot.create("spool"),
            TextureSlot.create("claw")
    );
    public static final ModelTemplate GRAPPLE_WINCH_3D_SPOOL_5 = copyTextureMap("grapple_winch_3d/spool_5", GRAPPLE_WINCH_3D_SPOOL_6);
    public static final ModelTemplate GRAPPLE_WINCH_3D_SPOOL_4 = copyTextureMap("grapple_winch_3d/spool_4", GRAPPLE_WINCH_3D_SPOOL_6);
    public static final ModelTemplate GRAPPLE_WINCH_3D_SPOOL_3 = copyTextureMap("grapple_winch_3d/spool_3", GRAPPLE_WINCH_3D_SPOOL_6);
    public static final ModelTemplate GRAPPLE_WINCH_3D_SPOOL_2 = copyTextureMap("grapple_winch_3d/spool_2", GRAPPLE_WINCH_3D_SPOOL_6);
    public static final ModelTemplate GRAPPLE_WINCH_3D_SPOOL_1 = copyTextureMap("grapple_winch_3d/spool_1", GRAPPLE_WINCH_3D_SPOOL_6);
    public static final ModelTemplate GRAPPLE_WINCH_3D_SPOOL_0 = copyTextureMap("grapple_winch_3d/spool_0", GRAPPLE_WINCH_3D_SPOOL_6);

    public static final ModelTemplate PIPE_MATRIX_U_BEND_X_POSITIVE = block("pipe_matrix_u_bend/x_positive",
            TextureSlot.TOP,
            KlaxonTextureSlots.U_BEND_CURVE,
            KlaxonTextureSlots.U_BEND_BOTTOM,
            KlaxonTextureSlots.U_BEND_SIDE,
            TextureSlot.PARTICLE
    );
    public static final ModelTemplate PIPE_MATRIX_U_BEND_X_NEGATIVE = copyDir("x_negative", PIPE_MATRIX_U_BEND_X_POSITIVE,
            TextureSlot.BOTTOM,
            KlaxonTextureSlots.U_BEND_CURVE,
            KlaxonTextureSlots.U_BEND_SIDE,
            KlaxonTextureSlots.U_BEND_SIDE,
            TextureSlot.PARTICLE
    );
    public static final ModelTemplate PIPE_MATRIX_U_BEND_Z_POSITIVE = copyDir("z_positive", PIPE_MATRIX_U_BEND_X_POSITIVE);
    public static final ModelTemplate PIPE_MATRIX_U_BEND_Z_NEGATIVE = copyDir("z_negative", PIPE_MATRIX_U_BEND_X_NEGATIVE);

    public static final ModelTemplate NORMAL_NETHER_REACTOR_CORE = block("nether_reactor_core/normal",
            KlaxonTextureSlots.CASING,
            KlaxonTextureSlots.CORE,
            TextureSlot.PARTICLE
    );
    public static final ModelTemplate ROTATED_NETHER_REACTOR_CORE = copyDir("rotated", NORMAL_NETHER_REACTOR_CORE);
    public static final ModelTemplate CUBE_FRONT_SIDE_TOP_BOTTOM_BACK = block(
            "template/front_side_top_bottom_back",
            TextureSlot.FRONT,
            TextureSlot.SIDE,
            TextureSlot.TOP,
            TextureSlot.BOTTOM,
            TextureSlot.BACK,
            TextureSlot.PARTICLE
    );
    public static final ModelTemplate CUBE_FRONT_SIDE_BACK = copyDir(
            "cube_front_side_back",
            CUBE_FRONT_SIDE_TOP_BOTTOM_BACK,
            TextureSlot.FRONT,
            TextureSlot.SIDE,
            TextureSlot.BACK,
            TextureSlot.PARTICLE
    );
    public static final ModelTemplate CUBE_TOP_SIDE_BOTTOM_TWO_LAYERS = copyDir(
            "cube_top_side_bottom_two_layers",
            CUBE_FRONT_SIDE_TOP_BOTTOM_BACK,
            KlaxonTextureSlots.TOP_LAYER_0,
            KlaxonTextureSlots.SIDE_LAYER_0,
            KlaxonTextureSlots.TOP_LAYER_1,
            KlaxonTextureSlots.SIDE_LAYER_1,
            KlaxonTextureSlots.BOTTOM_LAYER_1,
            TextureSlot.PARTICLE
    );

    public static Optional<ResourceLocation> id(ModelTemplate model) {
        return ((ModelTemplateAccessor) model).klaxon$getParent();
    }

    private static ModelTemplate copyDir(String path, ModelTemplate parent, TextureSlot... textureKeys) {
        Optional<ResourceLocation> parentId = id(parent);
        if (parentId.isEmpty()) {
            return new ModelTemplate(Optional.empty(), Optional.empty());
        }

        String parentPath = parentId.get().getPath();

        int lastParentSlash = parentPath.lastIndexOf('/');
        ResourceLocation modelId = KlaxonCommon.locate(
                lastParentSlash == -1
                        ? path
                        : parentPath.substring(0, lastParentSlash) + '/' + path
        );
        return new ModelTemplate(Optional.of(modelId), Optional.empty(), textureKeys);
    }

    private static ModelTemplate copyDir(String path, ModelTemplate parent) {
        return copyDir(path, parent, ((ModelTemplateAccessor)parent).klaxon$getRequiredTextures().toArray(new TextureSlot[] {}));
    }

    private static ModelTemplate block(String path, TextureSlot... requiredTextureKeys) {
        return new ModelTemplate(Optional.of(KlaxonCommon.locate("block/" + path)), Optional.empty(), requiredTextureKeys);
    }

    private static ModelTemplate item(String path, TextureSlot... requiredTextureKeys) {
        return new ModelTemplate(Optional.of(KlaxonCommon.locate("item/" + path)), Optional.empty(), requiredTextureKeys);
    }

    private static ModelTemplate copyTextureMap(String path, ModelTemplate model) {
        return new ModelTemplate(Optional.of(KlaxonCommon.locate("item/" + path)), Optional.empty(), ((ModelTemplateAccessor)model).klaxon$getRequiredTextures().toArray(new TextureSlot[]{}));
    }
}
