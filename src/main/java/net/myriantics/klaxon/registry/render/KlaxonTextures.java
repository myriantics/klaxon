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
    public static final Identifier GRAPPLE_WINCH_3D_CLAW = copyDir("claw", GRAPPLE_WINCH_3D_STRUCTURE);

    private static Identifier copyDir(String path, Identifier parent) {
        return copyDir(path, parent.getPath());
    }

    private static Identifier copyDir(String path, String parent) {
        int lastParentSlash = parent.lastIndexOf('/');
        return KlaxonCommon.locate(
                lastParentSlash == -1
                        ? path
                        : parent.substring(0, lastParentSlash) + "/" + path
        );
    }

    private static Identifier ofGeneric(String path) {
        return KlaxonCommon.locate(path);
    }

    private static Identifier ofItem(String path) {
        return KlaxonCommon.locate("item/" + path);
    }
}
