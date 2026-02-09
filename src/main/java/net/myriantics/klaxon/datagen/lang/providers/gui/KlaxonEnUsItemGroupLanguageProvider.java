package net.myriantics.klaxon.datagen.lang.providers.gui;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageSubProvider;
import net.myriantics.klaxon.registry.item.KlaxonItemGroups;

public final class KlaxonEnUsItemGroupLanguageProvider extends KlaxonEnUsLanguageSubProvider {
    public KlaxonEnUsItemGroupLanguageProvider(KlaxonEnUsLanguageProvider provider, FabricLanguageProvider.TranslationBuilder builder) {
        super(provider, builder);
    }

    @Override
    public void generate() {
        addItemGroup(KlaxonItemGroups.KLAXON_BUILDING_BLOCKS_ID, "KLAXON's Building Blocks");
        addItemGroup(KlaxonItemGroups.KLAXON_EQUIPMENT_ID, "KLAXON's Equipment");
        addItemGroup(KlaxonItemGroups.KLAXON_MACHINES_ID, "KLAXON's Machines");
        addItemGroup(KlaxonItemGroups.KLAXON_MATERIALS_ID, "KLAXON's Materials");
    }
}
