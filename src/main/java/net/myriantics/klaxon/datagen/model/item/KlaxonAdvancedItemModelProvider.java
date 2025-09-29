package net.myriantics.klaxon.datagen.model.item;

import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Model;
import net.myriantics.klaxon.datagen.model.KlaxonItemModelSubProvider;
import net.myriantics.klaxon.datagen.model.KlaxonModelProvider;
import net.myriantics.klaxon.registry.item.KlaxonItems;

public class KlaxonAdvancedItemModelProvider extends KlaxonItemModelSubProvider {
    public KlaxonAdvancedItemModelProvider(KlaxonModelProvider provider, ItemModelGenerator generator) {
        super(provider, generator);
    }

    @Override
    public void generateModels() {
        registerArmor(KlaxonItems.STEEL_HELMET);
        registerArmor(KlaxonItems.STEEL_CHESTPLATE);
        registerArmor(KlaxonItems.STEEL_LEGGINGS);
        registerArmor(KlaxonItems.STEEL_BOOTS);
        register2DGrappleWinch();
        register3DGrappleWinch();
    }
}
