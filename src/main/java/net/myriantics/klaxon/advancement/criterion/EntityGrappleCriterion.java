package net.myriantics.klaxon.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementCriteria;

import java.util.Optional;
import java.util.function.Predicate;

public class EntityGrappleCriterion extends AbstractCriterion<EntityGrappleCriterion.Conditions> {

    @Override
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity serverPlayer, Entity grappledEntity) {
        this.trigger(serverPlayer, (conditions) -> conditions.test(serverPlayer, grappledEntity));
    }

    public record Conditions(Optional<LootContextPredicate> player, Optional<EntityPredicate> entityPredicate) implements AbstractCriterion.Conditions {
        public static final Codec<EntityGrappleCriterion.Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player),
                EntityPredicate.CODEC.optionalFieldOf("entity_predicate").forGetter(Conditions::entityPredicate)
                ).apply(instance, Conditions::new)
        );

        public static AdvancementCriterion<Conditions> create(TagKey<EntityType<?>> tagKey) {
            return KlaxonAdvancementCriteria.ENTITY_GRAPPLE_CRITERION.create(new Conditions(
                    Optional.empty(),
                    Optional.of(new EntityPredicate.Builder().type(tagKey).build())
            ));
        }

        public static AdvancementCriterion<Conditions> create(EntityType<?> type) {
            return KlaxonAdvancementCriteria.ENTITY_GRAPPLE_CRITERION.create(new Conditions(
                    Optional.empty(),
                    Optional.of(new EntityPredicate.Builder().type(type).build())
            ));
        }

        boolean test(ServerPlayerEntity serverPlayer, Entity grappledEntity) {
            return entityPredicate.isEmpty() || entityPredicate.get().test(serverPlayer, grappledEntity);
        }
    }
}
