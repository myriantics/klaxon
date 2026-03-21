package net.myriantics.klaxon.datagen;

import net.minecraft.resources.ResourceLocation;

public class KlaxonDatagenCompatIds {
    public static final String CREATE_MOD_ID = "create";

    public static final ResourceLocation CREATE_BRASS_SHEET = locateCreate("brass_sheet");
    public static final ResourceLocation CREATE_BRASS_INGOT = locateCreate("brass_ingot");
    public static final ResourceLocation CREATE_PRECISION_MECHANISM = locateCreate("precision_mechanism");

    private static ResourceLocation locateCreate(String name) {
        return ResourceLocation.fromNamespaceAndPath(CREATE_MOD_ID, name);
    }


}
