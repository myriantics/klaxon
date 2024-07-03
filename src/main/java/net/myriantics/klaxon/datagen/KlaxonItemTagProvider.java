package net.myriantics.klaxon.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.myriantics.klaxon.util.KlaxonTags;

import java.util.concurrent.CompletableFuture;

public class KlaxonItemTagProvider extends FabricTagProvider.ItemTagProvider {

    public KlaxonItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(KlaxonTags.Items.BLAST_CHAMBER_FUEL_REGULAR)
                .add(Items.GUNPOWDER)
                .add(Items.FIREWORK_ROCKET);
        getOrCreateTagBuilder(KlaxonTags.Items.BLAST_CHAMBER_FUEL_SUPER)
                .add(Items.TNT)
                .add(Items.FIRE_CHARGE);
        getOrCreateTagBuilder(KlaxonTags.Items.BLAST_CHAMBER_FUEL_HYPER)
                .add(Items.END_CRYSTAL)
                .add(Items.TNT_MINECART);
    }
}
