package net.myriantics.klaxon.registry;

import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonEntityModelPartNames {

    public static final String HELMET_CREST_UPPER = register("helmet_crest_upper");
    public static final String HELMET_CREST_BACK = register("helmet_crest_back");
    public static final String HELMET_CREST_BASE_UPPER = register("helmet_crest_base_upper");
    public static final String HELMET_CREST_BASE_BACK = register("helmet_crest_base_back");
    public static final String HELMET_CREST_UPPER_SUPPORT = register("helmet_crest_upper_support");
    public static final String HELMET_CREST_EDGE_SUPPORT = register("helmet_crest_edge_support");
    public static final String HELMET_CREST_BACK_SUPPORT = register("helmet_crest_back_support");

    private static String register(String name) {
        return KlaxonCommon.locateAlt(name);
    }
}
