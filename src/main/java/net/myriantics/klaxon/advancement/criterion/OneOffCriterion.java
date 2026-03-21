package net.myriantics.klaxon.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementCriteria;

import java.util.Optional;

public class OneOffCriterion extends SimpleCriterionTrigger<OneOffCriterion.Conditions> {

    @Override
    public Codec<Conditions> codec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayer serverPlayer) {
        this.trigger(serverPlayer, (conditions -> true));
    }

    public record Conditions(Optional<ContextAwarePredicate> player) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(Conditions::player)
                ).apply(instance, OneOffCriterion.Conditions::new)
        );

        public static Criterion<OneOffCriterion.Conditions> createDeAnchorGrappleClaw() {
            return KlaxonAdvancementCriteria.DE_ANCHOR_GRAPPLE_WINCH_CLAW_CRITERION.createCriterion(new Conditions(
                    Optional.empty()
            ));
        }

        @Override
        public Optional<ContextAwarePredicate> player() {
            return player;
        }
    }
}
