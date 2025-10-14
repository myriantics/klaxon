package net.myriantics.klaxon.datagen.lang.providers.entity;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageSubProvider;
import net.myriantics.klaxon.registry.entity.KlaxonEntityAttributes;

public final class KlaxonEnUsEntityAttributeProvider extends KlaxonEnUsLanguageSubProvider {
    public KlaxonEnUsEntityAttributeProvider(KlaxonEnUsLanguageProvider provider, FabricLanguageProvider.TranslationBuilder builder) {
        super(provider, builder);
    }

    @Override
    public void generate() {
        generatePlayerAttributeTranslations();
    }

    private void generatePlayerAttributeTranslations() {
        addEntityAttribute(KlaxonEntityAttributes.WINCH_CABLE_LENGTH, "Winch Cable Length");
    }
}
