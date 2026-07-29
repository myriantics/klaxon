package net.myriantics.klaxon.datagen.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.myriantics.klaxon.tag.klaxon.KlaxonBiomeTags;

import java.util.concurrent.CompletableFuture;

public class KlaxonBiomeTagProvider extends FabricTagProvider<Biome> {
    public KlaxonBiomeTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.BIOME, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        getOrCreateTagBuilder(KlaxonBiomeTags.HAS_OMINOUS_DEEPSLATE_HALL)
                .addOptionalTag(BiomeTags.IS_OVERWORLD);
    }
}
