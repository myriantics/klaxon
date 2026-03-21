package net.myriantics.klaxon.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.item.equipment.tools.WrenchItem;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementCriteria;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class WrenchUsageCriterion extends SimpleCriterionTrigger<WrenchUsageCriterion.Conditions> {

    @Override
    public Codec<Conditions> codec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayer player, WrenchItem.UsageType usageType, BlockState targetState) {
        this.trigger(player, conditions -> conditions.matches(targetState, usageType));
    }

    public static record Conditions(Optional<ContextAwarePredicate> player, WrenchItem.UsageType usageType, Optional<TagKey<Block>> validBlocks) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<WrenchUsageCriterion.Conditions> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(WrenchUsageCriterion.Conditions::player),
                                WrenchItem.UsageType.CODEC.fieldOf("usage_type").forGetter(WrenchUsageCriterion.Conditions::usageType),
                                TagKey.hashedCodec(Registries.BLOCK).optionalFieldOf("valid_blocks").forGetter(WrenchUsageCriterion.Conditions::validBlocks)
                        )
                        .apply(instance, WrenchUsageCriterion.Conditions::new)
        );

        public static Criterion<WrenchUsageCriterion.Conditions> createRotation(@Nullable TagKey<Block> validBlocks) {
            return KlaxonAdvancementCriteria.WRENCH_USAGE_CRITERION.value().createCriterion(new WrenchUsageCriterion.Conditions(Optional.empty(), WrenchItem.UsageType.ROTATION, Optional.ofNullable(validBlocks)));
        }

        public static Criterion<WrenchUsageCriterion.Conditions> createPickup(@Nullable TagKey<Block> validBlocks) {
            return KlaxonAdvancementCriteria.WRENCH_USAGE_CRITERION.value().createCriterion(new WrenchUsageCriterion.Conditions(Optional.empty(), WrenchItem.UsageType.PICKUP, Optional.ofNullable(validBlocks)));
        }

        boolean matches(BlockState targetState, WrenchItem.UsageType usageType) {
            return usageType.equals(this.usageType) && (validBlocks.isEmpty() || targetState.is(validBlocks.get()));
        }
    }
}
