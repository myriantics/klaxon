package net.myriantics.klaxon.datagen.advancement;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.myriantics.klaxon.datagen.advancement.providers.KlaxonStageOneAdvancementProvider;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class KlaxonAdvancementProvider extends FabricAdvancementProvider {
    public KlaxonAdvancementProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(HolderLookup.Provider registryLookup, Consumer<AdvancementHolder> consumer) {
        AdvancementHolder preludeRoot = new KlaxonStageOneAdvancementProvider(consumer).generateAdvancements();
    }
}
