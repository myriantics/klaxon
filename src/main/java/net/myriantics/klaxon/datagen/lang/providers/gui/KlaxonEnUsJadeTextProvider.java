package net.myriantics.klaxon.datagen.lang.providers.gui;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageSubProvider;

public final class KlaxonEnUsJadeTextProvider extends KlaxonEnUsLanguageSubProvider {
    public KlaxonEnUsJadeTextProvider(KlaxonEnUsLanguageProvider provider, FabricLanguageProvider.TranslationBuilder builder) {
        super(provider, builder);
    }

    @Override
    public void generate() {
        generateTooltipTextTranslations();
        generateConfigTextTranslations();
    }

    private void generateTooltipTextTranslations() {
        addJadeTooltipText("blast_processor.explosion_power", "Explosion Power: %s");
        addJadeTooltipText("crop_growth_disabled", "Crop Growth Disabled");
        addJadeTooltipText("natural_crop_growth_inhibited", "Natural Crop Growth Inhibited");
    }

    private void generateConfigTextTranslations() {
        addJadeConfigText("deepslate_blast_processor", "Explosion Power: ");
        addJadeConfigText("crop_growth_disabled", "Crop Growth Disabled");
        addJadeConfigText("grapple_claw", "Grapple Claw");
    }
}
