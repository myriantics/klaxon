package net.myriantics.klaxon.registry;

import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonEntityModelPartNames {

    public static final String HELMET_CREST_DYEABLE = register("helmet_crest_dyeable");
    public static final String HELMET_CREST_STATIC_SUPPORT = register("helmet_crest_static");

    private static String register(String name) {
        return KlaxonCommon.locateAlt(name);
    }
}
