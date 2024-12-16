package net.myriantics.klaxon.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.myriantics.klaxon.block.KlaxonBlocks;
import net.myriantics.klaxon.item.KlaxonItems;
import net.myriantics.klaxon.util.KlaxonTags;

import java.util.concurrent.CompletableFuture;

public class KlaxonItemTagProvider extends FabricTagProvider.ItemTagProvider {

    public KlaxonItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(KlaxonTags.Items.STEEL_INGOTS)
                .add(KlaxonItems.STEEL_INGOT);

        getOrCreateTagBuilder(KlaxonTags.Items.STEEL_BLOCKS)
                .add(KlaxonBlocks.STEEL_BLOCK.asItem());

        getOrCreateTagBuilder(KlaxonTags.Items.STEEL_NUGGETS)
                .add(KlaxonItems.STEEL_NUGGET);
    }
}
