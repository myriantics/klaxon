package net.myriantics.klaxon.advancement.criterion.grapple_winch;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementCriteria;

import java.util.Optional;

public class EntityGrappleCriterion extends SimpleCriterionTrigger<EntityGrappleCriterion.Conditions> {

    @Override
    public Codec<Conditions> codec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayer serverPlayer, Entity grappledEntity) {
        this.trigger(serverPlayer, (conditions) -> conditions.test(serverPlayer, grappledEntity));
    }

    public record Conditions(Optional<ContextAwarePredicate> player, Optional<EntityPredicate> entityPredicate) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<EntityGrappleCriterion.Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(Conditions::player),
                EntityPredicate.CODEC.optionalFieldOf("entity_predicate").forGetter(Conditions::entityPredicate)
                ).apply(instance, Conditions::new)
        );

        public static Criterion<Conditions> create(TagKey<EntityType<?>> tagKey) {
            return KlaxonAdvancementCriteria.ENTITY_GRAPPLE_CRITERION.createCriterion(new Conditions(
                    Optional.empty(),
                    Optional.of(new EntityPredicate.Builder().of(tagKey).build())
            ));
        }

        public static Criterion<Conditions> create(EntityType<?> type) {
            return KlaxonAdvancementCriteria.ENTITY_GRAPPLE_CRITERION.createCriterion(new Conditions(
                    Optional.empty(),
                    Optional.of(new EntityPredicate.Builder().of(type).build())
            ));
        }

        boolean test(ServerPlayer serverPlayer, Entity grappledEntity) {
            return entityPredicate.isEmpty() || entityPredicate.get().matches(serverPlayer, grappledEntity);
        }
    }
}
