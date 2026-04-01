package net.myriantics.klaxon.datagen.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.myriantics.klaxon.mechanics.explosive_catalyst.AbstractExplosiveCatalystBehavior;
import net.myriantics.klaxon.registry.KlaxonRegistryKeys;
import net.myriantics.klaxon.registry.behavior.KlaxonExplosiveCatalystBehaviors;
import net.myriantics.klaxon.tag.klaxon.KlaxonExplosiveCatalystBehaviorTags;

import java.util.concurrent.CompletableFuture;

public class KlaxonExplosiveCatalystBehaviorTagProvider extends FabricTagProvider<AbstractExplosiveCatalystBehavior> {
    public KlaxonExplosiveCatalystBehaviorTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, KlaxonRegistryKeys.EXPLOSIVE_CATALYST_BEHAVIOR, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        getOrCreateTagBuilder(KlaxonExplosiveCatalystBehaviorTags.DOES_NOT_RUN_DISPENSER_EFFECTS)
                .add(KlaxonExplosiveCatalystBehaviors.WIND_CHARGE.value())
                .add(KlaxonExplosiveCatalystBehaviors.DRAGONS_BREATH.value())
                .add(KlaxonExplosiveCatalystBehaviors.FIREWORK_ROCKET.value());
    }
}
