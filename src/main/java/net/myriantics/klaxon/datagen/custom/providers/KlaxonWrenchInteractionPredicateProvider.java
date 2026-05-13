package net.myriantics.klaxon.datagen.custom.providers;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.myriantics.klaxon.datagen.custom.KlaxonDynamicRegistrySubProvider;
import net.myriantics.klaxon.mechanics.wrench.WrenchInteractionDenialPredicate;
import net.myriantics.klaxon.registry.KlaxonRegistries;

public class KlaxonWrenchInteractionPredicateProvider extends KlaxonDynamicRegistrySubProvider<WrenchInteractionDenialPredicate> {

    public KlaxonWrenchInteractionPredicateProvider(HolderLookup.Provider wrapperLookup, FabricDynamicRegistryProvider.Entries entries) {
        super(wrapperLookup, entries);
    }

    @Override
    protected void build() {
        StatePropertiesPredicate pistonPredicate = StatePropertiesPredicate.Builder.properties().hasProperty(BlockStateProperties.EXTENDED, true).build().get();
        this.add(Blocks.PISTON, pistonPredicate);
        this.add(Blocks.STICKY_PISTON, pistonPredicate);
    }

    private void add(Block block, StatePropertiesPredicate predicate) {
        this.add(block, new WrenchInteractionDenialPredicate(predicate));
    }

    private void add(Block block, WrenchInteractionDenialPredicate predicate) {
        this.add(
                ResourceKey.create(KlaxonRegistries.WRENCH_INTERACTION_DENIAL_PREDICATE, BuiltInRegistries.BLOCK.getKey(block)),
                predicate
        );
    }
}
