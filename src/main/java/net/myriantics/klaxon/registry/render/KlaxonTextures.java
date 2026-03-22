package net.myriantics.klaxon.registry.render;

import net.minecraft.resources.ResourceLocation;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonTextures {
    public static final ResourceLocation EMPTY = ofItem("empty");

    // 2D Grapple Winch
    public static final ResourceLocation GRAPPLE_WINCH_2D_UNLOADED = ofItem("grapple_winch/unloaded");
    public static final ResourceLocation GRAPPLE_WINCH_2D_LOADED = copyDir("loaded", GRAPPLE_WINCH_2D_UNLOADED);

    // 3D Grapple Winch
    public static final ResourceLocation GRAPPLE_WINCH_3D_STRUCTURE = ofItem("grapple_winch_3d/structure");
    public static final ResourceLocation GRAPPLE_WINCH_3D_SPOOL = copyDir("spool", GRAPPLE_WINCH_3D_STRUCTURE);
    public static final ResourceLocation GRAPPLE_WINCH_3D_SPOOL_RETRACTING = copyDir("spool_retracting", GRAPPLE_WINCH_3D_STRUCTURE);
    public static final ResourceLocation GRAPPLE_WINCH_STEEL_GRAPPLE_CLAW = copyDir("steel_grapple_claw", GRAPPLE_WINCH_3D_STRUCTURE);

    // Grapple Claw Entity
    public static final ResourceLocation STEEL_GRAPPLE_CLAW_ENTITY = ofEntity("grapple_claws/steel_grapple_claw");

    // Grapple Cable
    public static final ResourceLocation STEEL_CABLE_SEGMENT = ofEntity("grapple_cable/steel");

    // Helmet Crest
    public static final ResourceLocation HELMET_CREST = ofEntity("helmet_crest");

    // Steel Workbench
    public static final ResourceLocation STEEL_WORKBENCH_TOP = ofBlock("steel_workbench/top");
    public static final ResourceLocation STEEL_WORKBENCH_SIDE = copyDir("side", STEEL_WORKBENCH_TOP);

    // Steel Casing
    public static final ResourceLocation STEEL_CASING = ofBlock("steel_casing");
    public static final ResourceLocation CRUDE_STEEL_CASING = ofBlock("crude_steel_casing");

    // Nether Reaction EMI Background
    public static final ResourceLocation NETHER_REACTION_EMI_BACKGROUND = ofGui("emi/nether_reaction");

    public static ResourceLocation copyDir(String path, ResourceLocation parent) {
        return copyDir(path, parent.getPath());
    }

    public static ResourceLocation copyDir(String path, String parent) {
        int lastParentSlash = parent.lastIndexOf('/');
        return KlaxonCommon.locate(
                lastParentSlash == -1
                        ? path
                        : parent.substring(0, lastParentSlash) + "/" + path
        );
    }

    public static ResourceLocation ofGeneric(String path) {
        return KlaxonCommon.locate(path);
    }

    public static ResourceLocation ofItem(String path) {
        return KlaxonCommon.locate("item/" + path);
    }

    public static ResourceLocation ofBlock(String path) {
        return KlaxonCommon.locate("block/" + path);
    }

    public static ResourceLocation ofEntity(String path) {
        return KlaxonCommon.locate("entity/" + path);
    }

    private static ResourceLocation ofGui(String path) {
        return KlaxonCommon.locate("gui/" + path);
    }

    public static ResourceLocation decorate(ResourceLocation id) {
        return id.withPath((path) -> "textures/" + path + ".png");
    }
}
