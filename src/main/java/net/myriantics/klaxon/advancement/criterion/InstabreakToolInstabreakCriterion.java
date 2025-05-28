package net.myriantics.klaxon.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.myriantics.klaxon.registry.minecraft.KlaxonAdvancementCriteria;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class InstabreakToolInstabreakCriterion extends AbstractCriterion<InstabreakToolInstabreakCriterion.Conditions> {
    @Override
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, ItemStack instabreakingTool, BlockState instabrokenState) {
        this.trigger(player, conditions -> conditions.matches(instabreakingTool, instabrokenState));
    }

    public static record Conditions(Optional<LootContextPredicate> player, Optional<Ingredient> toolUsed, Optional<TagKey<Block>> validBlocks) implements AbstractCriterion.Conditions {
        public static final Codec<InstabreakToolInstabreakCriterion.Conditions> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(InstabreakToolInstabreakCriterion.Conditions::player),
                                Ingredient.ALLOW_EMPTY_CODEC.optionalFieldOf("tool_used").forGetter(InstabreakToolInstabreakCriterion.Conditions::toolUsed),
                                TagKey.codec(RegistryKeys.BLOCK).optionalFieldOf("valid_blocks").forGetter(InstabreakToolInstabreakCriterion.Conditions::validBlocks)
                        )
                        .apply(instance, InstabreakToolInstabreakCriterion.Conditions::new)
        );

        public static AdvancementCriterion<InstabreakToolInstabreakCriterion.Conditions> create(@Nullable Ingredient toolUsed,  @Nullable TagKey<Block> validBlocks) {
            return KlaxonAdvancementCriteria.INSTABREAK_TOOL_INSTABREAK_CRITERION.create(new InstabreakToolInstabreakCriterion.Conditions(Optional.empty(), Optional.ofNullable(toolUsed), Optional.ofNullable(validBlocks)));
        }

        boolean matches(ItemStack instabreakingTool, BlockState instabrokenState) {
            return (toolUsed.isEmpty() || toolUsed.get().test(instabreakingTool)) && (validBlocks.isEmpty() || instabrokenState.isIn(validBlocks.get()));
        }
    }
}
