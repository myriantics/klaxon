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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementCriteria;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class InstabreakToolInstabreakCriterion extends SimpleCriterionTrigger<InstabreakToolInstabreakCriterion.Conditions> {
    @Override
    public Codec<Conditions> codec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayer player, ItemStack instabreakingTool, BlockState instabrokenState) {
        this.trigger(player, conditions -> conditions.matches(instabreakingTool, instabrokenState));
    }

    public static record Conditions(Optional<ContextAwarePredicate> player, Optional<Ingredient> toolUsed, Optional<TagKey<Block>> validBlocks) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<InstabreakToolInstabreakCriterion.Conditions> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(InstabreakToolInstabreakCriterion.Conditions::player),
                                Ingredient.CODEC.optionalFieldOf("tool_used").forGetter(InstabreakToolInstabreakCriterion.Conditions::toolUsed),
                                TagKey.hashedCodec(Registries.BLOCK).optionalFieldOf("valid_blocks").forGetter(InstabreakToolInstabreakCriterion.Conditions::validBlocks)
                        )
                        .apply(instance, InstabreakToolInstabreakCriterion.Conditions::new)
        );

        public static Criterion<InstabreakToolInstabreakCriterion.Conditions> create(@Nullable Ingredient toolUsed,  @Nullable TagKey<Block> validBlocks) {
            return KlaxonAdvancementCriteria.INSTABREAK_TOOL_INSTABREAK_CRITERION.createCriterion(new InstabreakToolInstabreakCriterion.Conditions(Optional.empty(), Optional.ofNullable(toolUsed), Optional.ofNullable(validBlocks)));
        }

        boolean matches(ItemStack instabreakingTool, BlockState instabrokenState) {
            return (toolUsed.isEmpty() || toolUsed.get().test(instabreakingTool)) && (validBlocks.isEmpty() || instabrokenState.is(validBlocks.get()));
        }
    }
}
