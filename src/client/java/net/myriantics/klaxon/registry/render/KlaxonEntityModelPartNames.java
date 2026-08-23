package net.myriantics.klaxon.registry.render;

import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonEntityModelPartNames {

    public static final String HELMET_CREST_DYEABLE = register("helmet_crest_dyeable");
    public static final String HELMET_CREST_STATIC_SUPPORT = register("helmet_crest_static");
    public static final String EXPLOSIVE_DEEPSLATE_CHUNK_STATIC = register("explosive_deepslate_chunk_static");
    public static final String EXPLOSIVE_DEEPSLATE_CHUNK_TINTED = register("explosive_deepslate_chunk_tinted");

    private static String register(String name) {
        return KlaxonCommon.locateAlt(name);
    }
}
