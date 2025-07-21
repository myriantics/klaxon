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
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementCriteria;

import java.util.Optional;

public class BlockActivationCriterion extends AbstractCriterion<BlockActivationCriterion.Conditions> {

    @Override
    public Codec<BlockActivationCriterion.Conditions> getConditionsCodec() {
        return BlockActivationCriterion.Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, BlockState state) {
        this.trigger(player, conditions -> conditions.matches(state));
    }

    public static record Conditions(Optional<LootContextPredicate> player, TagKey<Block> blockTag) implements AbstractCriterion.Conditions {
        public static final Codec<BlockActivationCriterion.Conditions> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(BlockActivationCriterion.Conditions::player),
                                TagKey.codec(RegistryKeys.BLOCK).fieldOf("block").forGetter(BlockActivationCriterion.Conditions::blockTag)
                        )
                        .apply(instance, BlockActivationCriterion.Conditions::new)
        );

        public static AdvancementCriterion<BlockActivationCriterion.Conditions> create(TagKey<Block> blockTag) {
            return KlaxonAdvancementCriteria.BLOCK_ACTIVATION_CRITERION.create(new BlockActivationCriterion.Conditions(Optional.empty(), blockTag));
        }

        boolean matches(BlockState block) {
            return block.isIn(blockTag);
        }
    }
}
