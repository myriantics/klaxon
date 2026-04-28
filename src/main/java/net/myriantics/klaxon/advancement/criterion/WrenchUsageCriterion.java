package net.myriantics.klaxon.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.myriantics.klaxon.mechanics.wrench.WrenchUsageType;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementCriteria;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class WrenchUsageCriterion extends SimpleCriterionTrigger<WrenchUsageCriterion.Conditions> {

    @Override
    public Codec<Conditions> codec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayer player, BlockPos pos, WrenchUsageType usageType) {
        this.trigger(player, conditions -> conditions.matches(player.serverLevel(), pos, usageType));
    }

    public record Conditions(Optional<ContextAwarePredicate> player, Optional<WrenchUsageType> usageType, Optional<ItemPredicate> wrenchStackPredicate, Optional<BlockPredicate> interactedBlockPredicate) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<WrenchUsageCriterion.Conditions> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(Conditions::player),
                                WrenchUsageType.CODEC.optionalFieldOf("type").forGetter(Conditions::usageType),
                                ItemPredicate.CODEC.optionalFieldOf("wrench_stack_predicate").forGetter(Conditions::wrenchStackPredicate),
                                BlockPredicate.CODEC.optionalFieldOf("interacted_block_predicate").forGetter(Conditions::interactedBlockPredicate)
                        )
                        .apply(instance, WrenchUsageCriterion.Conditions::new)
        );

        public static Criterion<Conditions> createRotation(TagKey<Block> tagKey) {
            return createRotation(BlockPredicate.Builder.block().of(tagKey).build());
        }

        public static Criterion<Conditions> createRotation(@Nullable BlockPredicate predicate) {
            return createRotation(ItemPredicate.Builder.item().of(KlaxonItems.STEEL_WRENCH.value()).build(), predicate);
        }

        public static Criterion<Conditions> createRotation(@Nullable ItemPredicate wrenchStackPredicate, @Nullable BlockPredicate interactedBlockPredicate) {
            return create(WrenchUsageType.ROTATION, wrenchStackPredicate, interactedBlockPredicate);
        }

        public static Criterion<Conditions> createPickup(@Nullable BlockPredicate predicate) {
            return createPickup(ItemPredicate.Builder.item().of(KlaxonItems.STEEL_WRENCH.value()).build(), predicate);
        }

        public static Criterion<Conditions> createPickup(@Nullable ItemPredicate wrenchStackPredicate, @Nullable BlockPredicate interactedBlockPredicate) {
            return create(WrenchUsageType.PICKUP, wrenchStackPredicate, interactedBlockPredicate);
        }

        public static Criterion<Conditions> createWildcard(@Nullable BlockPredicate predicate) {
            return createWildcard(ItemPredicate.Builder.item().of(KlaxonItems.STEEL_WRENCH.value()).build(), predicate);
        }

        public static Criterion<Conditions> createWildcard(@Nullable ItemPredicate wrenchStackPredicate, @Nullable BlockPredicate interactedBlockPredicate) {
            return create(null, wrenchStackPredicate, interactedBlockPredicate);
        }

        public static Criterion<Conditions> create(@Nullable WrenchUsageType type, @Nullable ItemPredicate wrenchStackPredicate, @Nullable BlockPredicate interactedBlockPredicate) {
            return KlaxonAdvancementCriteria.WRENCH_USAGE.value().createCriterion(new Conditions(Optional.empty(), Optional.ofNullable(type), Optional.ofNullable(wrenchStackPredicate), Optional.ofNullable(interactedBlockPredicate)));
        }

        boolean matches(ServerLevel level, BlockPos pos, WrenchUsageType type) {
            if (this.usageType.isPresent() && !this.usageType.get().equals(type)) {
                return false;
            }

            if (this.interactedBlockPredicate.isPresent() && !this.interactedBlockPredicate.get().matches(level, pos)) {
                return false;
            }

            return true;
        }
    }
}
