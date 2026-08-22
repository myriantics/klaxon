package net.myriantics.klaxon.datagen.model.item;

import net.minecraft.data.models.ItemModelGenerators;
import net.myriantics.klaxon.datagen.model.KlaxonItemModelSubProvider;
import net.myriantics.klaxon.datagen.model.KlaxonModelProvider;
import net.myriantics.klaxon.registry.item.KlaxonItems;

public class KlaxonAdvancedItemModelProvider extends KlaxonItemModelSubProvider {
    public KlaxonAdvancedItemModelProvider(KlaxonModelProvider provider, ItemModelGenerators generator) {
        super(provider, generator);
    }

    @Override
    public void generateModels() {
        registerCrestedSteelHelmet();
        registerLighterItem(KlaxonItems.STEEL_LIGHTER);
        registerArmor(KlaxonItems.STEEL_HELMET);
        registerArmor(KlaxonItems.STEEL_CHESTPLATE);
        registerArmor(KlaxonItems.STEEL_LEGGINGS);
        registerArmor(KlaxonItems.STEEL_BOOTS);
        register2DGrappleWinch();
        register3DGrappleWinch();
        registerSpawnEgg(KlaxonItems.OMINOUS_DEEPSLATE_BLAST_PROCESSOR_SPAWN_EGG);
        registerBaseAndOverlay(KlaxonItems.EXPLOSIVE_DEEPSLATE_CHUNK.value());
    }
}
