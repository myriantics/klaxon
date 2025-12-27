package net.myriantics.klaxon.registry.render;

import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonTextures {
    public static final Identifier EMPTY = ofItem("empty");

    // 2D Grapple Winch
    public static final Identifier GRAPPLE_WINCH_2D_UNLOADED = ofItem("grapple_winch/unloaded");
    public static final Identifier GRAPPLE_WINCH_2D_LOADED = copyDir("loaded", GRAPPLE_WINCH_2D_UNLOADED);

    // 3D Grapple Winch
    public static final Identifier GRAPPLE_WINCH_3D_STRUCTURE = ofItem("grapple_winch_3d/structure");
    public static final Identifier GRAPPLE_WINCH_3D_SPOOL = copyDir("spool", GRAPPLE_WINCH_3D_STRUCTURE);
    public static final Identifier GRAPPLE_WINCH_3D_SPOOL_RETRACTING = copyDir("spool_retracting", GRAPPLE_WINCH_3D_STRUCTURE);
    public static final Identifier GRAPPLE_WINCH_STEEL_GRAPPLE_CLAW = copyDir("steel_grapple_claw", GRAPPLE_WINCH_3D_STRUCTURE);

    // Grapple Claw Entity
    public static final Identifier STEEL_GRAPPLE_CLAW_ENTITY = ofEntity("grapple_claws/steel_grapple_claw");

    // Grapple Cable
    public static final Identifier STEEL_CABLE_SEGMENT = ofEntity("grapple_cable/steel");

    // Helmet Crest
    public static final Identifier HELMET_CREST = ofEntity("helmet_crest");

    public static Identifier copyDir(String path, Identifier parent) {
        return copyDir(path, parent.getPath());
    }

    public static Identifier copyDir(String path, String parent) {
        int lastParentSlash = parent.lastIndexOf('/');
        return KlaxonCommon.locate(
                lastParentSlash == -1
                        ? path
                        : parent.substring(0, lastParentSlash) + "/" + path
        );
    }

    public static Identifier ofGeneric(String path) {
        return KlaxonCommon.locate(path);
    }

    public static Identifier ofItem(String path) {
        return KlaxonCommon.locate("item/" + path);
    }

    public static Identifier ofEntity(String path) {
        return KlaxonCommon.locate("entity/" + path);
    }

    public static Identifier decorate(Identifier id) {
        return id.withPath((path) -> "textures/" + path + ".png");
    }
}
