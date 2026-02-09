package net.myriantics.klaxon.mechanics.wrench;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.myriantics.klaxon.registry.KlaxonRegistryKeys;

import java.util.List;
import java.util.Optional;

public record WrenchInteractionDenialPredicate(StatePredicate predicate) {
    public static final Codec<WrenchInteractionDenialPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            StatePredicate.CODEC.fieldOf("properties").forGetter(WrenchInteractionDenialPredicate::predicate)
    ).apply(instance, WrenchInteractionDenialPredicate::new));

    public static final WrenchInteractionDenialPredicate NEVER = new WrenchInteractionDenialPredicate(new StatePredicate(List.of()));

    public boolean anyMatch(BlockState state) {
        for (StatePredicate.Condition property : predicate.conditions()) {
            if (property.test(state.getBlock().getStateManager(), state)) {
                return true;
            }
        }
        return false;
    }

    public static boolean wrenchInteractionBlocked(DynamicRegistryManager registryManager, BlockState state) {
        Optional<WrenchInteractionDenialPredicate> predicate = registryManager.get(KlaxonRegistryKeys.WRENCH_INTERACTION_DENIAL_PREDICATE).getOrEmpty(Registries.BLOCK.getId(state.getBlock()));

        // cancel behavior if the predicate passes
        return predicate.isPresent() && predicate.get().anyMatch(state);
    }
}
