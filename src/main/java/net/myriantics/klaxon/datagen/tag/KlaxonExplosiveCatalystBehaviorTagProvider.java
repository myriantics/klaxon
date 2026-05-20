package net.myriantics.klaxon.datagen.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystBehavior;
import net.myriantics.klaxon.registry.KlaxonRegistries;
import net.myriantics.klaxon.registry.explosive_catalyst.KlaxonExplosiveCatalystBehaviors;
import net.myriantics.klaxon.tag.klaxon.KlaxonExplosiveCatalystBehaviorTags;

import java.util.concurrent.CompletableFuture;

public class KlaxonExplosiveCatalystBehaviorTagProvider extends FabricTagProvider<ExplosiveCatalystBehavior> {
    public KlaxonExplosiveCatalystBehaviorTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, KlaxonRegistries.EXPLOSIVE_CATALYST_BEHAVIOR, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        getOrCreateTagBuilder(KlaxonExplosiveCatalystBehaviorTags.DOES_NOT_RUN_DISPENSER_EFFECTS)
                .addOptional(KlaxonExplosiveCatalystBehaviors.WIND_BURST)
                .addOptional(KlaxonExplosiveCatalystBehaviors.DRAGONS_BREATH)
                .addOptional(KlaxonExplosiveCatalystBehaviors.FIREWORK_ROCKET);
        getOrCreateTagBuilder(KlaxonExplosiveCatalystBehaviorTags.RUNS_DESTROY_BLOCK_EFFECTS_FOR_MODULAR_EXPLOSIVE_BLOCK)
                .addOptional(KlaxonExplosiveCatalystBehaviors.NO_OP)
                .addOptional(KlaxonExplosiveCatalystBehaviors.DRAGONS_BREATH);
        getOrCreateTagBuilder(KlaxonExplosiveCatalystBehaviorTags.UNUSABLE_FOR_BLAST_PROCESSING)
                .addOptional(KlaxonExplosiveCatalystBehaviors.WIND_BURST);
    }
}
