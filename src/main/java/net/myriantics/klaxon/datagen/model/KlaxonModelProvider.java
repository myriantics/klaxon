package net.myriantics.klaxon.datagen.model;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.customblocks.decor.HallnoxBulbBlock;
import net.myriantics.klaxon.datagen.model.block.KlaxonAdvancedBlockModelProvider;
import net.myriantics.klaxon.datagen.model.block.KlaxonSimpleBlockModelProvider;
import net.myriantics.klaxon.datagen.model.item.KlaxonAdvancedItemModelProvider;
import net.myriantics.klaxon.datagen.model.item.KlaxonSimpleItemModelProvider;
import net.myriantics.klaxon.registry.block.KlaxonBlockStateProperties;
import net.myriantics.klaxon.registry.block.KlaxonBlockFamilies;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import net.myriantics.klaxon.registry.item.KlaxonItems;

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
