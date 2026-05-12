package net.myriantics.klaxon.datagen.lang.providers.gui;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageSubProvider;
import net.myriantics.klaxon.registry.item.KlaxonArmorTrimMaterials;

public final class KlaxonEnUsTextLanguageProvider extends KlaxonEnUsLanguageSubProvider {
    public KlaxonEnUsTextLanguageProvider(KlaxonEnUsLanguageProvider provider, FabricLanguageProvider.TranslationBuilder builder) {
        super(provider, builder);
    }

    @Override
    public void generate() {
        generateTooltipText();
        generateMiscText();
        generateActionBartext();
    }

    private void generateActionBartext() {
        addActionBarText("catalyst_copy_from_to", "Cloned Explosive Catalyst Data from %1$s to %2$s");
    }

    private void generateTooltipText() {
        addTooltipText("recipe_output_lore.chance", "Chance: %1$s");
        addTooltipText("missing_block_item", "Missing Block Item");
        addTooltipText("heavy_equipment", "Heavy Equipment");
        addTooltipText("grapple_winch.projectile", "Projectile:");
        addTooltipText("grapple_winch.cable_length.prefix", "Cable Length: ");
        addTooltipText("grapple_winch.cable_length.display", "%1$s/%2$s");

        // modular explosive
        addTooltipText("modular_explosive_block_config.fuse_ticks", "Fuse Ticks: %s");
        addTooltipText("modular_explosive_block_config.ignition_ticks", "Ignition Ticks: %s");
        addTooltipText("modular_explosive_block_config.modify_world.true", "Modifies World");
        addTooltipText("modular_explosive_block_config.modify_world.false", "Does Not Modify World");

        // explosive catalyst
        addTooltipText("explosive_catalyst_data", "Explosive Catalyst:");
        addTooltipText("explosive_catalyst_data.catalyst_behavior", "- Behavior: %s");
        addTooltipText("explosive_catalyst_data.explosion_power", "- Explosion Power: %s");
        addTooltipText("explosive_catalyst_data.produces_fire", "- Produces Fire");
    }

    private void generateMiscText() {
        addText("blast_processor_creeper_name", "a Blast Processor mimicking a Charged Creeper. Expensive death right there.");
        addTrimMaterial(KlaxonArmorTrimMaterials.STEEL, "Steel Material");
    }
}
