package net.myriantics.klaxon.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.myriantics.klaxon.api.BlockIngredient;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementCriteria;

import java.util.Optional;

public class GrappleWinchVeinMineCriterion extends AbstractCriterion<GrappleWinchVeinMineCriterion.Conditions> {

    @Override
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity serverPlayer, BlockState veinMinedState) {
        this.trigger(serverPlayer, (conditions -> conditions.test(veinMinedState)));
    }

    public record Conditions(Optional<LootContextPredicate> player, Optional<BlockIngredient> blockIngredient) implements AbstractCriterion.Conditions {
        public static final Codec<GrappleWinchVeinMineCriterion.Conditions> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(GrappleWinchVeinMineCriterion.Conditions::player),
                                BlockIngredient.DISALLOW_EMPTY_CODEC.optionalFieldOf("block_ingredient").forGetter(Conditions::blockIngredient)
                        )
                        .apply(instance, GrappleWinchVeinMineCriterion.Conditions::new)
        );

        public static AdvancementCriterion<GrappleWinchVeinMineCriterion.Conditions> create(TagKey<Block> blockTag) {
            return KlaxonAdvancementCriteria.GRAPPLE_WINCH_VEIN_MINE_CRITERION.create(new Conditions(
                    Optional.empty(),
                    Optional.of(BlockIngredient.fromTag(blockTag))
            ));
        }

        public static AdvancementCriterion<GrappleWinchVeinMineCriterion.Conditions> create(Block... blocks) {
            return KlaxonAdvancementCriteria.GRAPPLE_WINCH_VEIN_MINE_CRITERION.create(new Conditions(
                    Optional.empty(),
                    Optional.of(BlockIngredient.ofBlocks(blocks))
            ));
        }

        boolean test(BlockState veinMinedState) {
            return blockIngredient.isEmpty() || blockIngredient.get().test(veinMinedState);
        }
    }
}
