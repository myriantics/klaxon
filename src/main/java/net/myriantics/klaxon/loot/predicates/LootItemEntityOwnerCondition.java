package net.myriantics.klaxon.loot.predicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.registry.loot.KlaxonLootItemConditions;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record LootItemEntityOwnerCondition(Optional<EntityPredicate> ownerPredicate, LootContext.EntityTarget ownerProviderTarget) implements LootItemCondition {

    public static final MapCodec<LootItemEntityOwnerCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            EntityPredicate.CODEC.optionalFieldOf("owner_predicate").forGetter(LootItemEntityOwnerCondition::ownerPredicate),
            LootContext.EntityTarget.CODEC.fieldOf("owner_provider_target").forGetter(LootItemEntityOwnerCondition::ownerProviderTarget)
    ).apply(instance, LootItemEntityOwnerCondition::new));

    @Override
    public LootItemConditionType getType() {
        return KlaxonLootItemConditions.ENTITY_OWNER.value();
    }

    @Override
    public boolean test(LootContext lootContext) {
        @Nullable Entity entity = lootContext.getParamOrNull(this.ownerProviderTarget.getParam());
        if (!(entity instanceof TraceableEntity traceable)) {
            return false;
        }
        Vec3 origin = lootContext.getParamOrNull(LootContextParams.ORIGIN);
        return this.ownerPredicate.isEmpty() || this.ownerPredicate.get().matches(lootContext.getLevel(), origin, traceable.getOwner());
    }
}
