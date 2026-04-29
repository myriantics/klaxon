package net.myriantics.klaxon.datagen.lang.providers.gui;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.myriantics.klaxon.compat.jade.providers.block.ExplosiveCatalystVesselBlockProvider;
import net.myriantics.klaxon.compat.jade.providers.block.MufflableBlockProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageSubProvider;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystVessel;

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
        addRawText(ExplosiveCatalystVesselBlockProvider.DATA_HIDDEN, "Hidden Or Missing");
        addRawText(ExplosiveCatalystVesselBlockProvider.EXPLOSION_POWER, "Explosion Power: %s");
        addRawText(MufflableBlockProvider.NOT_MUFFLED, "Not Muffled");
        addRawText(MufflableBlockProvider.MUFFLED, "Muffler: ");
        addJadeTooltipText("blast_processor.explosion_power", "Explosion Power: %s");
        addJadeTooltipText("crop_growth_disabled", "Crop Growth Disabled");
        addJadeTooltipText("natural_crop_growth_inhibited", "Natural Crop Growth Inhibited");
    }

    private void generateConfigTextTranslations() {
        addJadeConfigText("crop_growth_disabled", "Crop Growth Disabled");
        addJadeConfigText("grapple_claw", "Grapple Claw");
        addRawText(ExplosiveCatalystVesselBlockProvider.CONFIG, "Explosive Catalyst Vessels: ");
        addRawText(MufflableBlockProvider.CONFIG, "Mufflable Blocks");
    }
}
