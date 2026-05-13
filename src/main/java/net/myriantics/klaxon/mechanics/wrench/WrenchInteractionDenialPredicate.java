package net.myriantics.klaxon.mechanics.wrench;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.registry.KlaxonRegistries;

import java.util.List;
import java.util.Optional;

public record WrenchInteractionDenialPredicate(StatePropertiesPredicate predicate) {
    public static final Codec<WrenchInteractionDenialPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            StatePropertiesPredicate.CODEC.fieldOf("properties").forGetter(WrenchInteractionDenialPredicate::predicate)
    ).apply(instance, WrenchInteractionDenialPredicate::new));

    public static final WrenchInteractionDenialPredicate NEVER = new WrenchInteractionDenialPredicate(new StatePropertiesPredicate(List.of()));

    public boolean anyMatch(BlockState state) {
        for (StatePropertiesPredicate.PropertyMatcher property : predicate.properties()) {
            if (property.match(state.getBlock().getStateDefinition(), state)) {
                return true;
            }
        }
        return false;
    }

    public static boolean wrenchInteractionBlocked(RegistryAccess registryManager, BlockState state) {
        Optional<WrenchInteractionDenialPredicate> predicate = registryManager.registryOrThrow(KlaxonRegistries.WRENCH_INTERACTION_DENIAL_PREDICATE).getOptional(BuiltInRegistries.BLOCK.getKey(state.getBlock()));

        // cancel behavior if the predicate passes
        return predicate.isPresent() && predicate.get().anyMatch(state);
    }
}
