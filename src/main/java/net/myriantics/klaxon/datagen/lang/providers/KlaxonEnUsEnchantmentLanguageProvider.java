package net.myriantics.klaxon.datagen.lang.providers;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageSubProvider;
import net.myriantics.klaxon.registry.dynamic.KlaxonEnchantments;

public class KlaxonEnUsEnchantmentLanguageProvider extends KlaxonEnUsLanguageSubProvider {
    public KlaxonEnUsEnchantmentLanguageProvider(KlaxonEnUsLanguageProvider provider, FabricLanguageProvider.TranslationBuilder builder) {
        super(provider, builder);
    }

    @Override
    public void generate() {
        addEnchantment(KlaxonEnchantments.STREAMLINE, "Streamline");
    }
}
