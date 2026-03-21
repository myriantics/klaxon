package net.myriantics.klaxon.advancement.criterion.grapple_winch;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.recipe.BlockIngredient;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementCriteria;

import java.util.Optional;

public class GrappleWinchVeinMineCriterion extends SimpleCriterionTrigger<GrappleWinchVeinMineCriterion.Conditions> {

    @Override
    public Codec<Conditions> codec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayer serverPlayer, BlockState veinMinedState) {
        this.trigger(serverPlayer, (conditions -> conditions.test(veinMinedState)));
    }

    public record Conditions(Optional<ContextAwarePredicate> player, Optional<BlockIngredient> blockIngredient) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<GrappleWinchVeinMineCriterion.Conditions> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(GrappleWinchVeinMineCriterion.Conditions::player),
                                BlockIngredient.DISALLOW_EMPTY_CODEC.optionalFieldOf("block_ingredient").forGetter(Conditions::blockIngredient)
                        )
                        .apply(instance, GrappleWinchVeinMineCriterion.Conditions::new)
        );

        public static Criterion<GrappleWinchVeinMineCriterion.Conditions> create(TagKey<Block> blockTag) {
            return KlaxonAdvancementCriteria.GRAPPLE_WINCH_VEIN_MINE_CRITERION.value().createCriterion(new Conditions(
                    Optional.empty(),
                    Optional.of(BlockIngredient.fromTag(blockTag))
            ));
        }

        public static Criterion<GrappleWinchVeinMineCriterion.Conditions> create(Block... blocks) {
            return KlaxonAdvancementCriteria.GRAPPLE_WINCH_VEIN_MINE_CRITERION.value().createCriterion(new Conditions(
                    Optional.empty(),
                    Optional.of(BlockIngredient.ofBlocks(blocks))
            ));
        }

        boolean test(BlockState veinMinedState) {
            return blockIngredient.isEmpty() || blockIngredient.get().test(veinMinedState);
        }
    }
}
