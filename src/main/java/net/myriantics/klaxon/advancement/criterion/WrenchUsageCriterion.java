package net.myriantics.klaxon.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.myriantics.klaxon.item.equipment.tools.WrenchItem;
import net.myriantics.klaxon.registry.minecraft.KlaxonAdvancementCriteria;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class WrenchUsageCriterion extends AbstractCriterion<WrenchUsageCriterion.Conditions> {

    @Override
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, WrenchItem.UsageType usageType, BlockState targetState) {
        this.trigger(player, conditions -> conditions.matches(targetState, usageType));
    }

    public static record Conditions(Optional<LootContextPredicate> player, WrenchItem.UsageType usageType, Optional<TagKey<Block>> validBlocks) implements AbstractCriterion.Conditions {
        public static final Codec<WrenchUsageCriterion.Conditions> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(WrenchUsageCriterion.Conditions::player),
                                WrenchItem.UsageType.CODEC.fieldOf("usage_type").forGetter(WrenchUsageCriterion.Conditions::usageType),
                                TagKey.codec(RegistryKeys.BLOCK).optionalFieldOf("valid_blocks").forGetter(WrenchUsageCriterion.Conditions::validBlocks)
                        )
                        .apply(instance, WrenchUsageCriterion.Conditions::new)
        );

        public static AdvancementCriterion<WrenchUsageCriterion.Conditions> createRotation(@Nullable TagKey<Block> validBlocks) {
            return KlaxonAdvancementCriteria.WRENCH_USAGE_CRITERION.create(new WrenchUsageCriterion.Conditions(Optional.empty(), WrenchItem.UsageType.ROTATION, Optional.ofNullable(validBlocks)));
        }

        public static AdvancementCriterion<WrenchUsageCriterion.Conditions> createPickup(@Nullable TagKey<Block> validBlocks) {
            return KlaxonAdvancementCriteria.WRENCH_USAGE_CRITERION.create(new WrenchUsageCriterion.Conditions(Optional.empty(), WrenchItem.UsageType.PICKUP, Optional.ofNullable(validBlocks)));
        }

        boolean matches(BlockState targetState, WrenchItem.UsageType usageType) {
            return usageType.equals(this.usageType) && (validBlocks.isEmpty() || targetState.isIn(validBlocks.get()));
        }
    }
}
