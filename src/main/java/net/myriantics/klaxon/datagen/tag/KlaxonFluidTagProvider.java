package net.myriantics.klaxon.datagen.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalFluidTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.material.Fluids;
import net.myriantics.klaxon.tag.klaxon.KlaxonFluidTags;

import java.util.concurrent.CompletableFuture;

public class KlaxonFluidTagProvider extends FabricTagProvider.FluidTagProvider {
    public KlaxonFluidTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        getOrCreateTagBuilder(KlaxonFluidTags.COLD_FLUIDS)
                .forceAddTag(ConventionalFluidTags.WATER)
                .forceAddTag(ConventionalFluidTags.MILK);
        getOrCreateTagBuilder(KlaxonFluidTags.STEEL_BLAST_PROCESSOR_EXHAUST_OVERWRITABLE_ALLOWLIST)
                .add(Fluids.EMPTY);
    }
}
