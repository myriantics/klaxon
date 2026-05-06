package net.myriantics.klaxon.datagen.custom.providers;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.level.block.Blocks;
import net.myriantics.klaxon.datagen.custom.KlaxonDynamicRegistrySubProvider;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystBehavior;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystHandler;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystTransformer;
import net.myriantics.klaxon.mechanics.explosive_catalyst.transformer.*;
import net.myriantics.klaxon.recipe.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.registry.explosive_catalyst.KlaxonExplosiveCatalystBehaviors;
import net.myriantics.klaxon.registry.explosive_catalyst.KlaxonExplosiveCatalystHandlers;
import net.myriantics.klaxon.util.DimensionTypePredicate;

import java.util.Map;
import java.util.Optional;

public class KlaxonExplosiveCatalystBehaviorProvider extends KlaxonDynamicRegistrySubProvider<ExplosiveCatalystBehavior> {
    public KlaxonExplosiveCatalystBehaviorProvider(HolderLookup.Provider wrapperLookup, FabricDynamicRegistryProvider.Entries entries) {
        super(wrapperLookup, entries);
    }

    @Override
    protected void build() {
        this.add(
                KlaxonExplosiveCatalystBehaviors.NO_OP,
                KlaxonExplosiveCatalystHandlers.NO_OP
        );
        this.add(
                KlaxonExplosiveCatalystBehaviors.DEFAULT,
                KlaxonExplosiveCatalystHandlers.DEFAULT
        );

        // tnt
        this.add(
                KlaxonExplosiveCatalystBehaviors.TNT,
                KlaxonExplosiveCatalystHandlers.TNT
        );

        // fireworks
        FireworkExplosionExplosiveCatalystTransformer fireworkExplosionTransformer = new FireworkExplosionExplosiveCatalystTransformer(
                Map.of(
                        FireworkExplosion.Shape.LARGE_BALL, new ExplosiveCatalystData(1.3, true),
                        FireworkExplosion.Shape.CREEPER, new ExplosiveCatalystData(KlaxonExplosiveCatalystBehaviors.CHARGED_CREEPER_MIMIC, 6.0, false)
                ),
                ExplosiveCatalystData.ZERO,
                new ExplosiveCatalystData(KlaxonExplosiveCatalystBehaviors.RESPAWN_ANCHORLIKE, 1.3, true)
        );
        this.add(
                KlaxonExplosiveCatalystBehaviors.FIREWORK_STAR,
                KlaxonExplosiveCatalystHandlers.DEFAULT,
                fireworkExplosionTransformer
        );
        this.add(
                KlaxonExplosiveCatalystBehaviors.FIREWORK_ROCKET,
                KlaxonExplosiveCatalystHandlers.FIREWORK,
                new FireworksExplosiveCatalystTransformer(
                        fireworkExplosionTransformer,
                        new ExplosiveCatalystData(0.8, false)
                )
        );

        // beds and respawn anchors
        this.add(
                KlaxonExplosiveCatalystBehaviors.BEDLIKE,
                KlaxonExplosiveCatalystHandlers.DEFAULT,
                new DimensionTypeDependentExplosiveCatalystTransformer(
                        new DimensionTypePredicate(
                                Optional.empty(),
                                Optional.empty(),
                                Optional.of(false),
                                Optional.empty()
                        )
                )
        );
        this.add(
                KlaxonExplosiveCatalystBehaviors.RESPAWN_ANCHORLIKE,
                KlaxonExplosiveCatalystHandlers.DEFAULT,
                new DimensionTypeDependentExplosiveCatalystTransformer(
                        new DimensionTypePredicate(
                                Optional.empty(),
                                Optional.empty(),
                                Optional.empty(),
                                Optional.of(false)
                        )
                )
        );

        // wind burst
        this.add(
                KlaxonExplosiveCatalystBehaviors.WIND_BURST,
                KlaxonExplosiveCatalystHandlers.WIND_BURST
        );

        // dragons breath
        this.add(
                KlaxonExplosiveCatalystBehaviors.DRAGONS_BREATH,
                KlaxonExplosiveCatalystHandlers.DRAGONS_BREATH
        );

        // tnt minecart
        this.add(
                KlaxonExplosiveCatalystBehaviors.TNT_MINECART,
                KlaxonExplosiveCatalystHandlers.DEFAULT,
                new RedstoneSignalStrengthExplosiveCatalystTransformer(
                        1.0f, false
                )
        );

        // end crystal
        this.add(
                KlaxonExplosiveCatalystBehaviors.END_CRYSTAL,
                KlaxonExplosiveCatalystHandlers.DEFAULT,
                new BaseBlockStateExplosiveCatalystTransformer(
                        Optional.of(HolderSet.direct(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.BEDROCK))),
                        Optional.empty()
                )
        );

        // charged creeper mimic
        this.add(
                KlaxonExplosiveCatalystBehaviors.CHARGED_CREEPER_MIMIC,
                KlaxonExplosiveCatalystHandlers.CHARGED_CREEPER_MIMIC
        );
    }

    protected ExplosiveCatalystBehavior add(ResourceKey<ExplosiveCatalystBehavior> behaviorKey, Holder<ExplosiveCatalystHandler> handlerHolder, ExplosiveCatalystTransformer... transformers) {
        return this.add(behaviorKey, new ExplosiveCatalystBehavior(handlerHolder, transformers));
    }
}
