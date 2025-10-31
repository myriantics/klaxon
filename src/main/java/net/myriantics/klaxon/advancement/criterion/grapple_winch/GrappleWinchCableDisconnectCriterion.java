package net.myriantics.klaxon.advancement.criterion.grapple_winch;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawEntity;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementCriteria;

import java.util.Optional;

public class GrappleWinchCableDisconnectCriterion extends AbstractCriterion<GrappleWinchCableDisconnectCriterion.Conditions> {

    public void trigger(ServerPlayerEntity serverPlayer, GrappleClawEntity.CableDetachmentReason reason) {
        this.trigger(serverPlayer, (conditions) -> conditions.test(reason));
    }

    @Override
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public record Conditions(Optional<LootContextPredicate> player, Optional<GrappleClawEntity.CableDetachmentReason> detachmentReason) implements AbstractCriterion.Conditions {
        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                LootContextPredicate.CODEC.optionalFieldOf("player").forGetter(Conditions::player),
                GrappleClawEntity.CableDetachmentReason.CODEC.optionalFieldOf("cable_detachment_reason").forGetter(Conditions::detachmentReason)
        ).apply(instance, Conditions::new));

        public static AdvancementCriterion<Conditions> createWildcard() {
            return KlaxonAdvancementCriteria.GRAPPLE_WINCH_CABLE_DISCONNECT_CRITERION.create(new Conditions(Optional.empty(), Optional.empty()));
        }

        public static AdvancementCriterion<Conditions> create(GrappleClawEntity.CableDetachmentReason reason) {
            return KlaxonAdvancementCriteria.GRAPPLE_WINCH_CABLE_DISCONNECT_CRITERION.create(new Conditions(Optional.empty(), Optional.of(reason)));
        }

        @Override
        public Optional<LootContextPredicate> player() {
            return player;
        }

        public boolean test(GrappleClawEntity.CableDetachmentReason reason) {
            return detachmentReason.isEmpty() || detachmentReason.get().equals(reason);
        }
    }
}
