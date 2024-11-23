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

        getOrCreateTagBuilder(KlaxonTags.Items.SHIELD_DISABLING_MELEE_WEAPONS)
                .add(KlaxonItems.HAMMER);

        getOrCreateTagBuilder(KlaxonTags.Items.STEEL_BLOCKS)
                .add(KlaxonBlocks.STEEL_BLOCK.asItem());

        getOrCreateTagBuilder(KlaxonTags.Items.STEEL_NUGGETS)
                .add(KlaxonItems.STEEL_NUGGET);

        getOrCreateTagBuilder(KlaxonTags.Items.ITEM_EXPLOSION_POWER_EMI_OMITTED)
                .add(Items.CREEPER_SPAWN_EGG)
                .add(Items.GHAST_SPAWN_EGG)
                .add(Items.ENDER_DRAGON_SPAWN_EGG)
                .add(Items.WITHER_SPAWN_EGG);
    }
}
