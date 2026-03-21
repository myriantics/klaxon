package net.myriantics.klaxon.advancement.criterion.grapple_winch;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.myriantics.klaxon.mechanics.grapple_winch.CableDetachmentReason;
import net.myriantics.klaxon.mechanics.grapple_winch.GrapplingHook;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementCriteria;

import java.util.Optional;

public class GrappleWinchCableDisconnectCriterion extends SimpleCriterionTrigger<GrappleWinchCableDisconnectCriterion.Conditions> {

    public void trigger(ServerPlayer serverPlayer, GrapplingHook hook, CableDetachmentReason reason) {
        this.trigger(serverPlayer, (conditions) -> conditions.test(reason, serverPlayer, hook));
    }

    @Override
    public Codec<Conditions> codec() {
        return Conditions.CODEC;
    }

    public record Conditions(Optional<ContextAwarePredicate> player, Optional<CableDetachmentReason> detachmentReason, Optional<EntityPredicate> hookPredicate, Optional<EntityPredicate> playerPredicate) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ContextAwarePredicate.CODEC.optionalFieldOf("player").forGetter(Conditions::player),
                CableDetachmentReason.CODEC.optionalFieldOf("cable_detachment_reason").forGetter(Conditions::detachmentReason),
                EntityPredicate.CODEC.optionalFieldOf("hook_entity_predicate").forGetter(Conditions::hookPredicate),
                EntityPredicate.CODEC.optionalFieldOf("player_entity_predicate").forGetter(Conditions::playerPredicate)
        ).apply(instance, Conditions::new));

        public static Criterion<Conditions> createWildcard() {
            return KlaxonAdvancementCriteria.GRAPPLE_WINCH_CABLE_DISCONNECT_CRITERION.value().createCriterion(new Conditions(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
        }

        public static Criterion<Conditions> create(CableDetachmentReason reason) {
            return KlaxonAdvancementCriteria.GRAPPLE_WINCH_CABLE_DISCONNECT_CRITERION.value().createCriterion(new Conditions(Optional.empty(), Optional.of(reason), Optional.empty(), Optional.empty()));
        }

        public static Criterion<Conditions> createPlayer(CableDetachmentReason reason, EntityPredicate player) {
            return KlaxonAdvancementCriteria.GRAPPLE_WINCH_CABLE_DISCONNECT_CRITERION.value().createCriterion(new Conditions(Optional.empty(), Optional.of(reason), Optional.empty(), Optional.of(player)));
        }

        public static Criterion<Conditions> createHook(CableDetachmentReason reason, EntityPredicate hook) {
            return KlaxonAdvancementCriteria.GRAPPLE_WINCH_CABLE_DISCONNECT_CRITERION.value().createCriterion(new Conditions(Optional.empty(), Optional.of(reason), Optional.of(hook), Optional.empty()));
        }

        public static Criterion<Conditions> create(CableDetachmentReason reason, EntityPredicate player, EntityPredicate hook) {
            return KlaxonAdvancementCriteria.GRAPPLE_WINCH_CABLE_DISCONNECT_CRITERION.value().createCriterion(new Conditions(Optional.empty(), Optional.of(reason), Optional.of(hook), Optional.of(player)));
        }

        @Override
        public Optional<ContextAwarePredicate> player() {
            return player;
        }

        public boolean test(CableDetachmentReason reason, ServerPlayer player, GrapplingHook hook) {
            return (detachmentReason.isEmpty() || detachmentReason.get().equals(reason)) && (playerPredicate.isEmpty() || playerPredicate.get().matches(player, player)) && (hookPredicate.isEmpty() || hookPredicate.get().matches(player, hook.klaxon$asEntity()));
        }
    }
}
