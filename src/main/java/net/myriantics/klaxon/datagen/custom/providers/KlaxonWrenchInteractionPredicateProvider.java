package net.myriantics.klaxon.datagen.custom.providers;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.state.property.Properties;
import net.myriantics.klaxon.datagen.custom.KlaxonDynamicRegistrySubProvider;
import net.myriantics.klaxon.mechanics.wrench.WrenchInteractionDenialPredicate;
import net.myriantics.klaxon.registry.KlaxonRegistryKeys;

public class KlaxonWrenchInteractionPredicateProvider extends KlaxonDynamicRegistrySubProvider<WrenchInteractionDenialPredicate> {

    public KlaxonWrenchInteractionPredicateProvider(RegistryWrapper.WrapperLookup wrapperLookup, FabricDynamicRegistryProvider.Entries entries) {
        super(wrapperLookup, entries);
    }

    @Override
    protected void build() {
        StatePredicate pistonPredicate = StatePredicate.Builder.create().exactMatch(Properties.EXTENDED, true).build().get();
        this.add(Blocks.PISTON, pistonPredicate);
        this.add(Blocks.STICKY_PISTON, pistonPredicate);
    }

    private void add(Block block, StatePredicate predicate) {
        this.add(block, new WrenchInteractionDenialPredicate(predicate));
    }

    private void add(Block block, WrenchInteractionDenialPredicate predicate) {
        this.add(
                RegistryKey.of(KlaxonRegistryKeys.WRENCH_INTERACTION_DENIAL_PREDICATE, Registries.BLOCK.getId(block)),
                predicate
        );
    }
}
