package net.myriantics.klaxon.advancement.criterion.grapple_winch;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.myriantics.klaxon.mechanics.grapple_winch.CableDetachmentReason;
import net.myriantics.klaxon.mechanics.grapple_winch.GrapplingHook;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementCriteria;

import java.util.Optional;

public class GrappleWinchCableDisconnectCriterion extends AbstractCriterion<GrappleWinchCableDisconnectCriterion.Conditions> {

    public void trigger(ServerPlayerEntity serverPlayer, GrapplingHook hook, CableDetachmentReason reason) {
        this.trigger(serverPlayer, (conditions) -> conditions.test(reason, serverPlayer, hook));
    }

    @Override
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public record Conditions(Optional<LootContextPredicate> player, Optional<CableDetachmentReason> detachmentReason, Optional<EntityPredicate> hookPredicate, Optional<EntityPredicate> playerPredicate) implements AbstractCriterion.Conditions {
        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                LootContextPredicate.CODEC.optionalFieldOf("player").forGetter(Conditions::player),
                CableDetachmentReason.CODEC.optionalFieldOf("cable_detachment_reason").forGetter(Conditions::detachmentReason),
                EntityPredicate.CODEC.optionalFieldOf("hook_entity_predicate").forGetter(Conditions::hookPredicate),
                EntityPredicate.CODEC.optionalFieldOf("player_entity_predicate").forGetter(Conditions::playerPredicate)
        ).apply(instance, Conditions::new));

        public static AdvancementCriterion<Conditions> createWildcard() {
            return KlaxonAdvancementCriteria.GRAPPLE_WINCH_CABLE_DISCONNECT_CRITERION.create(new Conditions(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
        }

        public static AdvancementCriterion<Conditions> create(CableDetachmentReason reason) {
            return KlaxonAdvancementCriteria.GRAPPLE_WINCH_CABLE_DISCONNECT_CRITERION.create(new Conditions(Optional.empty(), Optional.of(reason), Optional.empty(), Optional.empty()));
        }

        public static AdvancementCriterion<Conditions> createPlayer(CableDetachmentReason reason, EntityPredicate player) {
            return KlaxonAdvancementCriteria.GRAPPLE_WINCH_CABLE_DISCONNECT_CRITERION.create(new Conditions(Optional.empty(), Optional.of(reason), Optional.empty(), Optional.of(player)));
        }

        public static AdvancementCriterion<Conditions> createHook(CableDetachmentReason reason, EntityPredicate hook) {
            return KlaxonAdvancementCriteria.GRAPPLE_WINCH_CABLE_DISCONNECT_CRITERION.create(new Conditions(Optional.empty(), Optional.of(reason), Optional.of(hook), Optional.empty()));
        }

        public static AdvancementCriterion<Conditions> create(CableDetachmentReason reason, EntityPredicate player, EntityPredicate hook) {
            return KlaxonAdvancementCriteria.GRAPPLE_WINCH_CABLE_DISCONNECT_CRITERION.create(new Conditions(Optional.empty(), Optional.of(reason), Optional.of(hook), Optional.of(player)));
        }

        @Override
        public Optional<LootContextPredicate> player() {
            return player;
        }

        public boolean test(CableDetachmentReason reason, ServerPlayerEntity player, GrapplingHook hook) {
            return (detachmentReason.isEmpty() || detachmentReason.get().equals(reason)) && (playerPredicate.isEmpty() || playerPredicate.get().test(player, player)) && (hookPredicate.isEmpty() || hookPredicate.get().test(player, hook.klaxon$asEntity()));
        }
    }
}
