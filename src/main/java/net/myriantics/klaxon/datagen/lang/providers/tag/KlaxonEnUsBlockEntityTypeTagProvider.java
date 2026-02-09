package net.myriantics.klaxon.datagen.lang.providers.tag;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageSubProvider;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockEntityTypeTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonDamageTypeTags;

public final class KlaxonEnUsBlockEntityTypeTagProvider extends KlaxonEnUsLanguageSubProvider {
    public KlaxonEnUsBlockEntityTypeTagProvider(KlaxonEnUsLanguageProvider provider, FabricLanguageProvider.TranslationBuilder builder) {
        super(provider, builder);
    }

    @Override
    public void generate() {
        addTag(KlaxonBlockEntityTypeTags.NETHER_REACTION_OVERWRITABLE, "Nether Reaction Overwritable Block Entities");
    }
}
