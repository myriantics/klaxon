package net.myriantics.klaxon.datagen.lang.providers.tag;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageSubProvider;
import net.myriantics.klaxon.tag.klaxon.KlaxonEntityTypeTags;

public final class KlaxonEnUsEntityTypeTagLanguageProvider extends KlaxonEnUsLanguageSubProvider {
    public KlaxonEnUsEntityTypeTagLanguageProvider(KlaxonEnUsLanguageProvider provider, FabricLanguageProvider.TranslationBuilder builder) {
        super(provider, builder);
    }

    @Override
    public void generate() {
        addTag(KlaxonEntityTypeTags.FERROMAGNETIC_ENTITIES, "Ferromagnetic Entities");
        addTag(KlaxonEntityTypeTags.HEAVY_HITTERS, "Heavy Hitting Entities");
        addTag(KlaxonEntityTypeTags.WALLJUMP_MOVABLE_ENTITIES, "Hammer Walljump Movable Entities");

        addTag(KlaxonEntityTypeTags.HEAVY_ENTITIES, "Heavy Entities");
        addTag(KlaxonEntityTypeTags.LIGHT_ENTITIES, "Light Entities");

        addTag(KlaxonEntityTypeTags.GRAPPLE_CLAW_HOOKING_DENYLIST, "Grapple Claw Hooking Denylist");
    }
}
