package net.myriantics.klaxon.datagen.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalFluidTags;
import net.minecraft.registry.RegistryWrapper;
import net.myriantics.klaxon.tag.klaxon.KlaxonFluidTags;

import java.util.concurrent.CompletableFuture;

public class KlaxonFluidTagProvider extends FabricTagProvider.FluidTagProvider {
    public KlaxonFluidTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(KlaxonFluidTags.COLD_FLUIDS)
                .forceAddTag(ConventionalFluidTags.WATER)
                .forceAddTag(ConventionalFluidTags.MILK);
    }
}
