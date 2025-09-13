package net.myriantics.klaxon.datagen;

import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;

public class KlaxonDatagenCompatIds {
    public static final String CREATE_MOD_ID = "create";

    public static final Identifier CREATE_BRASS_SHEET = locateCreate("brass_sheet");
    public static final Identifier CREATE_BRASS_INGOT = locateCreate("brass_ingot");
    public static final Identifier CREATE_PRECISION_MECHANISM = locateCreate("precision_mechanism");

    private static Identifier locateCreate(String name) {
        return Identifier.of(CREATE_MOD_ID, name);
    }


}
