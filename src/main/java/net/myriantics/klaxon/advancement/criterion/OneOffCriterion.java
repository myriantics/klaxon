package net.myriantics.klaxon.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementCriteria;

import java.util.Optional;

public class OneOffCriterion extends AbstractCriterion<OneOffCriterion.Conditions> {

    @Override
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity serverPlayer) {
        this.trigger(serverPlayer, (conditions -> true));
    }

    public record Conditions(Optional<LootContextPredicate> player) implements AbstractCriterion.Conditions {
        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player)
                ).apply(instance, OneOffCriterion.Conditions::new)
        );

        public static AdvancementCriterion<OneOffCriterion.Conditions> createGrappleWinchIntentionallyDisconnectCable() {
            return KlaxonAdvancementCriteria.INTENTIONALLY_DISCONNECT_GRAPPLE_WINCH_CABLE_CRITERION.create(new Conditions(
                    Optional.empty()
            ));
        }

        public static AdvancementCriterion<OneOffCriterion.Conditions> createDeAnchorGrappleClaw() {
            return KlaxonAdvancementCriteria.DE_ANCHOR_GRAPPLE_WINCH_CLAW_CRITERION.create(new Conditions(
                    Optional.empty()
            ));
        }

        @Override
        public Optional<LootContextPredicate> player() {
            return player;
        }
    }
}
