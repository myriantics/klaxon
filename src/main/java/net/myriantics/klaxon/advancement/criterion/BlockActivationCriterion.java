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
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementCriteria;

import java.util.Optional;

public class BlockActivationCriterion extends SimpleCriterionTrigger<BlockActivationCriterion.Conditions> {

    @Override
    public Codec<BlockActivationCriterion.Conditions> codec() {
        return BlockActivationCriterion.Conditions.CODEC;
    }

    public void trigger(ServerPlayer player, BlockState state) {
        this.trigger(player, conditions -> conditions.matches(state));
    }

    public static record Conditions(Optional<ContextAwarePredicate> player, TagKey<Block> blockTag) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<BlockActivationCriterion.Conditions> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(BlockActivationCriterion.Conditions::player),
                                TagKey.hashedCodec(Registries.BLOCK).fieldOf("block").forGetter(BlockActivationCriterion.Conditions::blockTag)
                        )
                        .apply(instance, BlockActivationCriterion.Conditions::new)
        );

        public static Criterion<BlockActivationCriterion.Conditions> create(TagKey<Block> blockTag) {
            return KlaxonAdvancementCriteria.BLOCK_ACTIVATION_CRITERION.createCriterion(new BlockActivationCriterion.Conditions(Optional.empty(), blockTag));
        }

        boolean matches(BlockState block) {
            return block.is(blockTag);
        }
    }
}
