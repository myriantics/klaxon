package net.myriantics.klaxon.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.block.BlockState;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

public class GrappleWinchVeinMineCriterion extends AbstractCriterion<GrappleWinchVeinMineCriterion.Conditions> {

    @Override
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity serverPlayer, BlockState veinMinedState) {
        this.trigger(serverPlayer, (conditions -> conditions.test(veinMinedState)));
    }

    public record Conditions(Optional<LootContextPredicate> player, StatePredicate statePredicate) implements AbstractCriterion.Conditions {
        public static final Codec<GrappleWinchVeinMineCriterion.Conditions> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(GrappleWinchVeinMineCriterion.Conditions::player),
                                StatePredicate.CODEC.fieldOf("block_predicate").forGetter(GrappleWinchVeinMineCriterion.Conditions::statePredicate)
                        )
                        .apply(instance, GrappleWinchVeinMineCriterion.Conditions::new)
        );

        boolean test(BlockState veinMinedState) {
            return statePredicate.test(veinMinedState.getBlock().getStateManager(), veinMinedState);
        }
    }
}
