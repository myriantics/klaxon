package net.myriantics.klaxon.datagen.model;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.myriantics.klaxon.datagen.model.block.KlaxonAdvancedBlockModelProvider;
import net.myriantics.klaxon.datagen.model.block.KlaxonSimpleBlockModelProvider;
import net.myriantics.klaxon.datagen.model.item.KlaxonAdvancedItemModelProvider;
import net.myriantics.klaxon.datagen.model.item.KlaxonSimpleItemModelProvider;

public class KlaxonModelProvider extends FabricModelProvider {
    public KlaxonModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator generator) {
        new KlaxonSimpleBlockModelProvider(this, generator).generateModels();
        new KlaxonAdvancedBlockModelProvider(this, generator).generateModels();
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        new KlaxonSimpleItemModelProvider(this, itemModelGenerator).generateModels();
        new KlaxonAdvancedItemModelProvider(this, itemModelGenerator).generateModels();
    }

}
