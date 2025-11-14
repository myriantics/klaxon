package net.myriantics.klaxon.datagen.lang.providers.gui;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageSubProvider;

public final class KlaxonEnUsTextLanguageProvider extends KlaxonEnUsLanguageSubProvider {
    public KlaxonEnUsTextLanguageProvider(KlaxonEnUsLanguageProvider provider, FabricLanguageProvider.TranslationBuilder builder) {
        super(provider, builder);
    }

    @Override
    public void generate() {
        generateTooltipText();
        generateMiscText();
    }

    private void generateTooltipText() {
        addTooltipText("innate_enchantment_prefix", "Innate %1$s");
        addTooltipText("recipe_output_lore", "Chance: %1$s");
        addTooltipText("missing_block_item", "Missing Block Item");
        addTooltipText("heavy_equipment", "Heavy Equipment");
        addTooltipText("grapple_winch.projectile", "Projectile:");
        addTooltipText("grapple_winch.cable_length.prefix", "Cable Length: ");
        addTooltipText("grapple_winch.cable_length.display", "%1$s/%2$s");
    }

    private void generateMiscText() {
        addText("blast_processor_creeper_name", "a Blast Processor mimicking a Charged Creeper. Expensive death right there.");
    }
}
